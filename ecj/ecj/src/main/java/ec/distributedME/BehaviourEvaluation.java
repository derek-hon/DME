package ec.distributedME;

import ec.EvolutionState;
import java.awt.image.BufferedImage;
/**
 * BehaviourEvaluation.java
 *
 * Created: 25 January, 2022
 * @author Derek Hon
 * @version 1.0
 */

public abstract class BehaviourEvaluation {
    /** setup method to use behaviour evaluation freely*/
    public abstract void setup(int[][] pixels);
    
    /**
     * This method is to calculate the behaviours for the individual then returns
     * these values to be used.
     *
     * @param ind Individual to calculate behaviours for
     * @return double[] behaviour values
     */
    public abstract double[] individualBehaviourCalculation(EvolutionState state, Elite ind);
} // BehaviourEvaluation
