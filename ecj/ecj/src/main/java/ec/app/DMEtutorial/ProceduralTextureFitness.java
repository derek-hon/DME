package ec.app.DMEtutorial;

import ec.util.Parameter;
import ec.util.ParameterDatabase;

import java.awt.image.BufferedImage;
// import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import ec.gp.koza.*;
import ec.distributedME.*;
import ec.EvolutionState;
import ec.app.DMEtutorial.Fitness.AKAZEFitness;
import ec.app.DMEtutorial.Fitness.Haar;
import ec.app.DMEtutorial.Fitness.MultiScaleSSIM;
import ec.app.DMEtutorial.Fitness.PSDExtensions;
import ec.app.DMEtutorial.Fitness.SIFTFitness;
import ec.app.DMEtutorial.Fitness.Haar.ImageQueryData;
import ec.app.experiments.DME.ImageLoad;
// import ec.app.DMEtutorial.SSIM.SsimException;


/**
 * ProceduralTextureFitness.java Created: 05/05/2020 By: Derek Hon
 **/

@SuppressWarnings("serial")
public class ProceduralTextureFitness extends ImageLoad {
    
    MultiScaleSSIM SSIM;
    Haar Target;
    ImageQueryData TargetData;
    SIFTFitness sift;
    AKAZEFitness akazeFitness;
    double targetSIFT = 0;
    
    int[][] targetData;

    ArrayList<Integer> waveletIslands;
    ArrayList<Integer> SSIMIslands;
    ArrayList<Integer> SIFTIslands;
    ArrayList<Integer> AKAZEIslands;
    boolean website = false;

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        ParameterDatabase parameters = new ParameterDatabase();
        website = parameters.getBoolean(new Parameter("website"), null, false);
        if (website) {
            if (parameters.exists(new Parameter("SIFTIslands"), null) &&
                parameters.exists(new Parameter("SSIMIslands"), null) &&
                parameters.exists(new Parameter("waveletIslands"), null) &&
                parameters.exists(new Parameter("AKAZEIslands"), null)) {
                    websiteIslandSetup(parameters);
            }
        }

        targetData = PSDExtensions.ImageToInt2D(TargetImage, null);
        sift = new SIFTFitness(targetData, targetData);
        targetSIFT = sift.getSIFT();
        Target = new Haar(state, TargetImage);
        TargetData = Target.getHaarData();
    }

    private void websiteIslandSetup(ParameterDatabase parameters) {
        String temp = parameters.getString(new Parameter("SIFTIslands"), null);
        temp = temp.trim();
        String[] tempArr = temp.split(" ");

        for (int i = 0 ; i < tempArr.length ; i ++) {
            SIFTIslands.add(Integer.parseInt(tempArr[i]));
        }

        temp = parameters.getString(new Parameter("SSIMIslands"), null);
        temp = temp.trim();
        tempArr = temp.split(" ");
        
        for (int i = 0 ; i < tempArr.length ; i ++) {
            SSIMIslands.add(Integer.parseInt(tempArr[i]));
        }

        temp = parameters.getString(new Parameter("waveletIslands"), null);
        temp = temp.trim();
        tempArr = temp.split(" ");
        
        for (int i = 0 ; i < tempArr.length ; i ++) {
            waveletIslands.add(Integer.parseInt(tempArr[i]));
        }

        temp = parameters.getString(new Parameter("AKAZEIslands"), null);
        temp = temp.trim();
        tempArr = temp.split(" ");
        
        for (int i = 0 ; i < tempArr.length ; i ++) {
            AKAZEIslands.add(Integer.parseInt(tempArr[i]));
        }
    }

    @Override
    public void evaluate(EvolutionState state, Elite ind, int submap, int threadnum) {
        if (!ind.evaluated) {
            if (!(ind instanceof Elite))
                state.output.fatal("" + this.getClass() + " expects individuals of type Elite.");

            // int[] luminance = new int[width * height];
            super.evaluate(state, ind, submap, threadnum);
            
            double fitness = 0.0;

            if (website) {
                int[][] solutionPixels = ind.getPixels();
                if (SIFTIslands.contains(submap)) {
                    sift = new SIFTFitness(targetData, solutionPixels);
                    fitness = targetSIFT / sift.getSIFT();
                }
                else if (SSIMIslands.contains(submap)) {
                    SSIM = new MultiScaleSSIM(targetData, solutionPixels);
                    fitness = SSIM.getMultiScaleSSIM();
                }
                else if (waveletIslands.contains(submap)) {
                    Haar solution = new Haar(state, ind);
                    fitness = TargetData.compareTo(solution.getHaarData());
                }
                else if (AKAZEIslands.contains(submap)) {
                    akazeFitness = new AKAZEFitness(targetData, solutionPixels);
                    fitness = akazeFitness.getMatches();
                }
            }

            KozaFitness f = ((KozaFitness) ind.fitness);
            f.setStandardizedFitness(state, fitness);
            
            ind.evaluated = true;
        } // if
    } // evaluate

    @Override
    public void describe(final EvolutionState state, final Elite ind, final int submap, final int threadnum, final int log) {
        super.describe(state, ind, submap, threadnum, log);
    }
}
