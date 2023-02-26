package ec.app.DMEtutorial.BehaviourEvaluations;

import ec.EvolutionState;
import ec.distributedME.BehaviourEvaluation;
import ec.distributedME.Elite;

public class AKAZEBehaviour extends BehaviourEvaluation {
    int[][] pixels;
    int submap = 0;
    
    
    public void setup() {
        
    }
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
                return new double[] {ColourEvaluationFunctions.colourEntropy(ind.getPixels()), ColourEvaluationFunctions.intensitySkewness(ind.getPixels())};
            case 1:
                return new double[] {ColourEvaluationFunctions.averageHue(ind.getPixels()), ColourEvaluationFunctions.intensityKurtosis(ind.getPixels())};
            case 2:
                return new double[] {GeometryEvaluationFunctions.LineDetection(ind.getPixels()), GeometryEvaluationFunctions.BlobDetection(ind.getPixels())};
            default:
                state.output.fatal("Default reached in  behaviour evaluation, AKAZEBehaviour");
        }

        return null;
    }
}
