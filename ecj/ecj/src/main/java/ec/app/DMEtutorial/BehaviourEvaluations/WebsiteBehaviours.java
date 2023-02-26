package ec.app.DMEtutorial.BehaviourEvaluations;

import ec.EvolutionState;
import ec.distributedME.BehaviourEvaluation;
import ec.distributedME.Behaviours;
import ec.distributedME.DistributedMEEvolutionState;
import ec.distributedME.Elite;
import ec.distributedME.EliteMap;

public class WebsiteBehaviours extends BehaviourEvaluation {
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
        double[] behaviourValues = new double[2];
        
        EliteMap eliteMap = ((DistributedMEEvolutionState) state).distributedMap.getEliteMAP(submap);
        Behaviours[] behaviours = eliteMap.getBehaviours();
        
        for (int i = 0 ; i < 2 ; i ++) {
            if (behaviours[i].getName().toLowerCase().contains("intensity"))
                behaviourValues[i] = ColourEvaluationFunctions.averageIntensity(pixels);
            else if (behaviours[i].getName().toLowerCase().contains("hue"))
                behaviourValues[i] = ColourEvaluationFunctions.averageHue(pixels);
            else if (behaviours[i].getName().toLowerCase().contains("saturation"))
                behaviourValues[i] = ColourEvaluationFunctions.averageSaturation(pixels);
            else if (behaviours[i].getName().toLowerCase().contains("contour"))
                behaviourValues[i] = GeometryEvaluationFunctions.ContourDetection(pixels);
            else if (behaviours[i].getName().toLowerCase().contains("corner"))
                behaviourValues[i] = GeometryEvaluationFunctions.CornerDetection(pixels);
            else if (behaviours[i].getName().toLowerCase().contains("line"))
                behaviourValues[i] = GeometryEvaluationFunctions.LineDetection(pixels);
        }


        // for (int i = 0 ; i < totalSubmaps ; i ++) {
        //     // if (submap == )
        // }
        // switch (submap) {
        //     case 0:
        //         return new double[] {ColourEvaluationFunctions.colourEntropy(ind.getPixels()), ColourEvaluationFunctions.averageIntensity(ind.getPixels())};
        //     case 1:
        //         return new double[] {ColourEvaluationFunctions.colourEntropy(ind.getPixels()), GeometryEvaluationFunctions.ContourDetection(ind.getPixels())};
        //     case 2:
        //         return new double[] {GeometryEvaluationFunctions.LineDetection(ind.getPixels()), GeometryEvaluationFunctions.BlobDetection(ind.getPixels())};
        //     default:
        //         state.output.fatal("Default reached in  behaviour evaluation, AKAZEBehaviour");
        // }

        return behaviourValues;
    }
}
