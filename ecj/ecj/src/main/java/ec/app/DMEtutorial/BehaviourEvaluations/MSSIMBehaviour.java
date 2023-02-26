package ec.app.DMEtutorial.BehaviourEvaluations;

import ec.EvolutionState;
import ec.distributedME.BehaviourEvaluation;
import ec.distributedME.Elite;

public class MSSIMBehaviour extends BehaviourEvaluation {
    int[][] pixels;
    int submap = 0;
    
    @Override
    public void setup(int[][] values) {
        pixels = values.clone();
    }

    
    public void setup(int submap) {
        this.submap = submap;
    }

    @Override
    public double[] individualBehaviourCalculation(EvolutionState state, Elite ind) {
        switch (submap) {
            case 0:
                return new double[] {ColourEvaluationFunctions.colourEntropy(ind.getPixels()), GeometryEvaluationFunctions.CornerDetection(ind.getPixels())};
            case 1:
                return new double[] {ColourEvaluationFunctions.averageSaturation(ind.getPixels()), GeometryEvaluationFunctions.ContourDetection(ind.getPixels())};
            case 2:
                return new double[] {GeometryEvaluationFunctions.LineDetection(ind.getPixels()), ColourEvaluationFunctions.averageHue(ind.getPixels())};
            default:
                state.output.fatal("Default reached in  behaviour evaluation, AKAZEBehaviour");
        }

        return null;
    }
}
