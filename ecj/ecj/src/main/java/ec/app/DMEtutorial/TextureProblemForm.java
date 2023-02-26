package ec.app.DMEtutorial;

import java.awt.image.BufferedImage;

import ec.EvolutionState;
import ec.distributedME.*;

/**
 * Based off of Michael Gircys PSD Evolutionary Art
 */
public interface TextureProblemForm extends MapElitesProblemForm {

    // Generate the texture phenotype in a displayable format.
    // Separated, and with parameterized size, to allow for calls from the window
    // (ie. user wants to look at image details, or save a huge image)
    /**
     * @param state     evolution state
     * @param ind       individual
     * @param subpop    subpopulation
     * @param threadnum which thread
     * @param size
     * @return image phenotype
     */
    
    public BufferedImage RenderImage(final EvolutionState state, Elite ind, int
    threadnum, int size);
    
    public int[] RenderColour(EvolutionState state, Elite ind, int threadnum, int size);

    public double[] Get_Current_Pos();

    public void Set_Current_Pos(double[] p);
}