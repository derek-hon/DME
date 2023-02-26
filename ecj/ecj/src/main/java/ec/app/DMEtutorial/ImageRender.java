/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.app.DMEtutorial;

import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.awt.Graphics2D;

import static ec.app.DMEtutorial.CoordinateVariables.*;

import java.awt.Color;

import ec.EvolutionState;
import ec.app.DMEtutorial.BehaviourEvaluations.AKAZEBehaviour;
import ec.app.DMEtutorial.BehaviourEvaluations.MSSIMBehaviour;
import ec.app.DMEtutorial.BehaviourEvaluations.WebsiteBehaviours;
import ec.app.DMEtutorial.Fitness.MSSIM;
import ec.app.DMEtutorial.Fitness.PSDExtensions;
import ec.app.experiments.Evaluations.EvaluationFunctions;
import ec.util.Parameter;
import ec.gp.koza.KozaFitness;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.imageio.ImageIO;

import ec.distributedME.*;

/**
 * ImageRender.java
 * 
 * Repurposed and modified for use within MAP-Elites by Derek Hon.
 * 
 * @author Michael Gircys
 */

public class ImageRender extends MapElitesProblem implements TextureProblemForm {
    protected static int Default_Image_Size = 256;
    private double[] Current_Pos = new double[CoordinateVariables.values().length];

    private static final String P_MIN_X = "min_x";
    private static final String P_MAX_X = "max_x";
    private static final String P_MIN_Y = "min_y";
    private static final String P_MAX_Y = "max_y";
    private static final String P_RENDER = "render";
    private static final String P_RENDER_SUB = "render-sub";
    private static final String P_RENDER_THREAD = "render-threads";
    public static final String P_SAVE_PATH = "save-path";
    public static final String P_GRADIENT_DEBUG = "gradient-debug";

    public String savePath;

    public static final String P_BEHAVIOUR_SOURCE = "behaviour-source";
    BehaviourEvaluation bEvaluation;

    private double yMin = 0, yMax = 0, xMin = 0, xMax = 0;
    public int[] colours = null;
    int size;
    int renderThreads = 1;
    int renderSub = 0;
    public BufferedImage img;
    boolean describeGraph = false;
    boolean gradientDebug;
    boolean distributed;
    boolean renderImage = false;

    int blueMask = 0xFF0000, greenMask = 0xFF00, redMask = 0xFF;

    // ThreadPool pool = new ThreadPool();

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        Parameter def = base;

        xMin = state.parameters.getDouble(base.push(P_MIN_X), def.push(P_MIN_X));
        xMax = state.parameters.getDouble(base.push(P_MAX_X), def.push(P_MAX_X));
        yMin = state.parameters.getDouble(base.push(P_MIN_Y), def.push(P_MIN_Y));
        yMax = state.parameters.getDouble(base.push(P_MAX_Y), def.push(P_MAX_Y));

        Parameter d = new Parameter("distributed");
        distributed = state.parameters.getBoolean(d, null, false);

        if (state.parameters.exists(base.push(P_RENDER_THREAD), null)) {
            renderThreads = state.parameters.getInt(base.push(P_RENDER_THREAD), null, 1);
        }

        if (state.parameters.exists(base.push(P_RENDER), null)) {
            renderImage = state.parameters.getBoolean(base.push(P_RENDER), null, false);
            if (state.parameters.exists(base.push(P_RENDER).push(P_RENDER_SUB), null)) {
                renderSub = state.parameters.getInt(base.push(P_RENDER).push(P_RENDER_SUB), null, 0);
            }
        }

        size = state.parameters.getInt(base.push("imagesize"), base.push("imagesize"), 0);

        if (state.parameters.exists(MapElitesDefaults.base().push(P_GRADIENT_DEBUG), null)) {
            gradientDebug = state.parameters.getBoolean(MapElitesDefaults.base().push(P_GRADIENT_DEBUG), null, false);
        } else {
            gradientDebug = false;
        }

        // verify our input is the right class (or subclasses from it)
        if (!(input instanceof MultiData))
            state.output.fatal("GPData class must subclass from " + MultiData.class, base.push(P_DATA), null);

        savePath = state.parameters.getString(base.push(P_SAVE_PATH), null);
    }

    @Override
    public void evaluate(final EvolutionState state, final Elite ind, final int threadnum, int submap) {
        if (ind.evaluated)
            return;
        
        BehaviourEvaluation bEval = ((DistributedMEEvolutionState) state).behaviourEvaluations[submap];
        RenderIndividual(state, ind, threadnum, size);

        //Too lazy to change interface and resulting classes right now
        ((WebsiteBehaviours) bEval).setup(submap);
        double[] indBehaviours = bEval.individualBehaviourCalculation(state, ind);
        ind.setBehaviours(indBehaviours);
    } // evaluate

    // We expect a [0.0,1.0] range. Make it [0,255] (fairly).
    private int FitToChannelRange(double v) {
        v = Math.min(Math.max(0.0, v), 1.0);
        return (int) Math.ceil(v * 255.0);
    }

    public void RenderIndividual(EvolutionState state, Elite ind, int threadnum, int size) {
        final int Image_Size_X = size;
        final int Image_Size_Y = size;

        MultiData input = (MultiData) this.input;

        double xIncrement = (xMax - xMin) / Image_Size_X;
        double yIncrement = (yMax - yMin) / Image_Size_Y;

        double textureY = yMin;
        double textureX = 0;

        // BufferedImage outImage = new BufferedImage(Image_Size_X, Image_Size_Y,
        //         BufferedImage.TYPE_INT_RGB);
        // this.colours = new int[Image_Size_X * Image_Size_Y];
        int[][] test = new int[Image_Size_X][Image_Size_Y];
        // BufferedImage outImage = new BufferedImage(50, 50,
        // BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < Image_Size_Y; y++) {
            textureX = xMin;
            for (int x = 0; x < Image_Size_X; x++) {
                int r, g, b, rgb = 0, gray=0;

                Current_Pos[X.ordinal()] = textureX;
                Current_Pos[Y.ordinal()] = textureY;

                if (ind.trees.length < 3) {
                    ind.trees[0].child.eval(state, threadnum, input, stack, ind, this);
                    gray = FitToChannelRange(input.GetD());
                    rgb = (gray<<16) + (gray<<8) + (gray);
                }
                else {
                    // Multiple colour channels.
                    ind.trees[0].child.eval(state, threadnum, input, stack, ind, this);
                    r = FitToChannelRange(input.GetD());
                    ind.trees[1].child.eval(state, threadnum, input, stack, ind, this);
                    g = FitToChannelRange(input.GetD());
                    ind.trees[2].child.eval(state, threadnum, input, stack, ind, this);
                    b = FitToChannelRange(input.GetD());
                    rgb = (r<<16) + (g<<8) + (b);
                }
                // this.colours[y * Image_Size_X + x] = rgb;
                test[x][y] = rgb;
                // outImage.setRGB(x, y, rgb);
                textureX += xIncrement;
            } // end for
            textureY += yIncrement;
        } // end for
        ind.setPixels(test);
        // return outImage;
    }

    public BufferedImage RenderImage(EvolutionState state, Elite ind, int threadnum, int size) {
        final int Image_Size_X = size;
        final int Image_Size_Y = size;

        MultiData input = (MultiData) this.input;

        double xIncrement = (xMax - xMin) / Image_Size_X;
        double yIncrement = (yMax - yMin) / Image_Size_Y;

        double textureY = yMin;
        double textureX = 0;

        BufferedImage outImage = new BufferedImage(Image_Size_X, Image_Size_Y,
                BufferedImage.TYPE_INT_RGB);
        int[][] test = new int[Image_Size_X][Image_Size_Y];

        for (int y = 0; y < Image_Size_Y; y++) {
            textureX = xMin;
            for (int x = 0; x < Image_Size_X; x++) {
                int r, g, b, rgb = 0, gray=0;

                Current_Pos[X.ordinal()] = textureX;
                Current_Pos[Y.ordinal()] = textureY;

                if (ind.trees.length < 3) {
                    ind.trees[0].child.eval(state, threadnum, input, stack, ind, this);
                    gray = FitToChannelRange(input.GetD());
                    rgb = (gray<<16) + (gray<<8) + (gray);
                }
                else {
                    // Multiple colour channels.
                    ind.trees[0].child.eval(state, threadnum, input, stack, ind, this);
                    r = FitToChannelRange(input.GetD());
                    ind.trees[1].child.eval(state, threadnum, input, stack, ind, this);
                    g = FitToChannelRange(input.GetD());
                    ind.trees[2].child.eval(state, threadnum, input, stack, ind, this);
                    b = FitToChannelRange(input.GetD());
                    rgb = (r<<16) + (g<<8) + (b);
                }
                // this.colours[y * Image_Size_X + x] = rgb;
                test[x][y] = rgb;
                outImage.setRGB(x, y, rgb);
                textureX += xIncrement;
            } // end for
            textureY += yIncrement;
        } // end for
        ind.setPixels(test);
        return outImage;
    }

    public int[] RenderColour(EvolutionState state, Elite ind, int threadnum, int size) {
        final int Image_Size_X = size;
        final int Image_Size_Y = size;
        this.colours = new int[Image_Size_X * Image_Size_Y];

        MultiData input = (MultiData) (this.input);

        
        
        final double xInc = 2.0 / ((double) Image_Size_X);
		final double yInc = 2.0 / ((double) Image_Size_Y);

        // double textureY = yMin;
        // double textureX = 0;

        int[] data = new int[Image_Size_X * Image_Size_Y];
        for (int y = 0; y < Image_Size_Y; y++) {
            // textureX = xMin;
            for (int x = 0; x < Image_Size_X; x++) {
                int r, g, b, rgb = 0, gray = 0;

                Current_Pos[X.ordinal()] = -1.0 + xInc * x;
                Current_Pos[Y.ordinal()] = -1.0 + yInc * y;

                if (ind.trees.length < 3) {
                    // 1 colour channel
                    ind.trees[0].child.eval(state, threadnum, input, stack, ind, this);
                    gray = FitToChannelRange(input.GetD());
                    rgb = (gray<<16) + (gray<<8) + (gray);
                }
                else {
                    // Multiple colour channels.
                    ind.trees[0].child.eval(state, threadnum, input, stack, ind, this);
                    r = FitToChannelRange(input.GetD());
                    ind.trees[1].child.eval(state, threadnum, input, stack, ind, this);
                    g = FitToChannelRange(input.GetD());
                    ind.trees[2].child.eval(state, threadnum, input, stack, ind, this);
                    b = FitToChannelRange(input.GetD());
                    rgb = (r<<16) + (g<<8) + (b);
                }
                data[y * Image_Size_X + x] = rgb;
                // textureX += xIncrement;
            } // end for
            // textureY += yIncrement;
        } // end for
        return data.clone();
    }

    public double[] Get_Current_Pos() {
        return Current_Pos;
    }

    public void Set_Current_Pos(double[] p) {
        Current_Pos = p;
    }

    @Override
    public Object clone() {
        ImageRender o = (ImageRender) super.clone();
        return o;
    }

    @Override
    public void describe(final EvolutionState state, final Elite best, final int submap, final int threadnum,
            final int log) {
        // ArrayList map = state.getStateMap().getMap();
        // int[] pixels;
        best.evaluated = false;
        File dir = new File(System.getProperty("user.dir") + savePath);
        
        if (!dir.exists())
            dir.mkdir();
        // For matplotlib grouped boxplots
        String[] behaviourOutput = new String[3];
        for (int i = 0; i < behaviourOutput.length; i++)
            behaviourOutput[i] = "";

        
        generateMAP(state, submap, threadnum);
        
        // try {
        //     // printAllBehaviours(state, log, best.getPixels());
        //     int[][] values = best.getPixels();
        //     BufferedImage bestImage = imageFromPixels(state, values);
        //     File output = new File(dir,
        //             "best_" + state.parameters.getInt(new Parameter("run"), null) + "_" + best.getKey() + "_submap_" + submap + "_" +
        //                     (int) ((KozaFitness) best.fitness).standardizedFitness() +
        //                     ".png");
        //     ImageIO.write(bestImage, "png", output);
        // } catch (IOException e) {
        //     state.output.fatal(
        //             "Error while saving best individual as image: " + e);
        // }
    }

    private void printAllBehaviours(EvolutionState state, final int log, final int[][] pixels) {
        EvaluationFunctions functions = new EvaluationFunctions();
        state.output.println("Red: " + functions.averageRed(pixels), log);
        state.output.println("Green: " + functions.averageGreen(pixels), log);
        state.output.println("Blue: " + functions.averageBlue(pixels), log);
        state.output.println("Entropy: " + functions.entropy(state, pixels), log);
        state.output.println("Luminosity: " + functions.averageLuminance(state, pixels), log);
        state.output.println("Skewness: " + functions.skewness(state, pixels), log);
        state.output.println("Kurtosis: " + functions.kurtosis(state, pixels), log);
    }

    private BufferedImage debugColour(BufferedImage image, String name) {
        BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0 ; x < image.getWidth() ; x ++) {
            for (int y = 0 ; y < image.getHeight() ; y ++) {
                Color color = new Color(image.getRGB(x, y));
                output.setRGB(x, y, setColour(name, color));
            }
        }
        return output;
    }

    private int setColour(String name, Color colour) {
        if (name.equalsIgnoreCase("Mean_Red_Mean_Blue")) {
            return new Color(colour.getRed(), 0, colour.getBlue()).getRGB();
        }
        else if (name.equalsIgnoreCase("Mean_Red_Mean_Green")) {
            return new Color(colour.getRed(), colour.getGreen(), 0).getRGB();
        }
        else if (name.equalsIgnoreCase("Mean_Green_Mean_Blue")) {
            return new Color(0 , colour.getGreen(), colour.getBlue()).getRGB();
        }
        return 0;
    }

    private BufferedImage imageFromPixels(EvolutionState state, int[][] pixels) {
        BufferedImage outputImg = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        for (int imageX = 0 ; imageX < size ; imageX ++) {
            for (int imageY = 0 ; imageY < size ; imageY ++) {
                outputImg.setRGB(imageX, imageY, pixels[imageX][imageY]);
                // if (e.getPixels()[x][y] !=  result.getRGB(x, y))
                //     state.output.fatal("different rgb");
            }
        }
        return outputImg;
    }

    private void generateMAP(EvolutionState state, int submap, int threadnum) {
        BufferedImage emptyImage = null;
        EliteMap eliteMap = ((DistributedMEEvolutionState) state).distributedMap.subMAPs[submap];
        String name = eliteMap.getName();

        File dir = new File(System.getProperty("user.dir") + savePath + "test/");
        if (!dir.exists())
            dir.mkdir();
        try {
            emptyImage = scaleImage(ImageIO.read(new File(System.getProperty("user.dir") + "/empty.png")));
        } catch (IOException e) {
            state.output.fatal(
                    "Error while saving image: " + System.getProperty("user.dir") + "/empty.png" + "\n"
                            + e);
        }
        int counter = 0;
        Behaviours[] behaviours = eliteMap.getBehaviours();
        try {
            int behaviourOneLimit = behaviours[0].totalLimits(), behaviourTwoLimit = behaviours[1].totalLimits();
            BufferedImage mapImage = new BufferedImage(size * behaviourTwoLimit, size * behaviourOneLimit, BufferedImage.TYPE_INT_RGB);
            BufferedImage[] chunks = new BufferedImage[behaviourOneLimit * behaviourTwoLimit];
                
            int yLimit = behaviours[0].totalLimits();
            int xLimit = behaviours[1].totalLimits();
            
            for (int y = (yLimit - 1) ; y >= 0 ; y --) {
                for (int x = 0 ; x < xLimit ; x ++) {
                    String key = behaviours[0].getName() + behaviours[0].getLimit(y) + behaviours[1].getName()
                            + behaviours[1].getLimit(x);
                    double[] limits = new double[]{behaviours[0].getLimit(y), behaviours[1].getLimit(x)};
                    Elite temp = null;

                    BufferedImage cellImage = ((temp = eliteMap.getEliteByKey(key)) == null) ? emptyImage : PSDExtensions.Int2DToImage(temp.getPixels());
                    chunks[counter] = cellImage;
                        
                    counter ++;
                }
            }
            counter = 0;
            for (int y = 0; y < behaviourOneLimit; y++) {
                for (int x = 0; x < behaviourTwoLimit; x++) {
                    mapImage.createGraphics().drawImage(chunks[counter], null, size * x, size * y);
                    // debugMap.createGraphics().drawImage(chunksMap[counter], 50 * j, 50 * k, null);
                    counter++;
                }
            }
            ImageIO.write(mapImage, "png", new File(dir + "/" + state.parameters.getString(new Parameter("mapname"), null) + "_" + submap + ".png"));          
        } catch (IOException e) {
            state.output.fatal("Error while saving map as image:\n" + e);
        }
    }

    // Source: https://stackoverflow.com/a/11367424
    private BufferedImage scaleImage(BufferedImage input) {
        BufferedImage output = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2d = output.createGraphics();
        graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.drawImage(input, 0, 0, size, size, null);
        graphics2d.dispose();

        return output;
    }

    /**
     * https://stackoverflow.com/a/18037185
     * @param Red
     * @param Green
     * @param Blue
     * @return
     */
    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.
    
        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
}
