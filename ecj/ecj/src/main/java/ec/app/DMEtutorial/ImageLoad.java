package ec.app.DMEtutorial;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;
import ec.distributedME.*;
import ec.EvolutionState;
import ec.app.DMEtutorial.Fitness.PSDExtensions;
import ec.app.DMEtutorial.Fitness.PSDTools;
import ec.util.Parameter;
import java.nio.file.*;

@SuppressWarnings("serial")
public abstract class ImageLoad extends ImageRender {

    static final String P_TARGET = "target";

    // PSD library is expecting a square image (double[][]) with specific size.
    protected static int PSDTools_Image_Size = PSDTools.SideLength;

    public BufferedImage TargetImage = null;
    public double[][] TargetImage_Gray = null;
    public File targetFile = null;

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        // Default_Image_Size = imageSize;
        Parameter p = new Parameter("map");

        state.output.message("Obtaining target image...");
        // Load targets from existing image
        if (state.parameters.exists(base.push("target"), null)) {
            // Load Image
            targetFile = state.parameters.getFile(base.push(P_TARGET), null);
            // TargetImage = ImageIO.read(targetFile);
            // state.output.message(targetFile.toPath().toString());
            try {
                // File targetFile = state.parameters.getFile(base.push(P_TARGET),null);
                BufferedImage image = ImageIO.read(targetFile);
                TargetImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                for (int x = 0 ; x < image.getWidth() ; x ++) {
                    for (int y = 0 ; y < image.getHeight() ; y ++) {
                        TargetImage.setRGB(x, y, image.getRGB(x, y));
                    }
                }
                if (TargetImage == null) throw new Exception("TargetImage null in " + this.getClass());
            } catch (Exception e) {
                Path currentRelativePath = Paths.get("");
                String s = currentRelativePath.toAbsolutePath().toString();
                state.output.message("relative path" + s + "\nfile path: " + targetFile.toPath().toString());
                state.output.fatal("Error loading target image: " + e.toString() + " in " + s);
            }

            int iSide = PSDTools_Image_Size;
            if( TargetImage.getHeight() != iSide || TargetImage.getWidth() != iSide )
                state.output.fatal("Error loading target image: problem requires dimensions of " + iSide + "x" + iSide);

            PSDExtensions.ImageToArrayStrategyDefault = PSDExtensions.ImageToArrayStrategy.Y601;
            TargetImage_Gray = PSDExtensions.ImageToDouble2D(TargetImage, null);
            state.output.message("Successfully loaded image");
        } // if
    } // setup
} // ImageLoad
