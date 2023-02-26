package ec.app.DMEtutorial;

import ec.util.Parameter;
import ec.gp.koza.*;
import ec.distributedME.*;
import ec.EvolutionState;
import ec.app.DMEtutorial.Fitness.PSDExtensions;
import ec.app.DMEtutorial.Fitness.SIFTFitness;
/**
 * ProceduralTextureFitness.java Created: 05/05/2020 By: Derek Hon
 **/
import ec.app.DMEtutorial.Fitness.SURFFitness;

@SuppressWarnings("serial")
public class ProceduralTextureSURF extends ImageLoad {

    double[] TargetSample;
    SURFFitness surf;
    String savePath = "";
    int[][] targetData;

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        savePath = state.parameters.getString(base.push(P_SAVE_PATH), null);

        targetData = PSDExtensions.ImageToInt2D(TargetImage, null);
    }

    @Override
    public void evaluate(EvolutionState state, Elite ind, int submap, int threadnum) {
        if(ind.evaluated) return;
        super.evaluate(state, ind, submap, threadnum);

        surf = new SURFFitness(targetData, ind.getPixels());
        double fitness = surf.getSurfValue();

        KozaFitness f = ((KozaFitness)ind.fitness);
        f.setStandardizedFitness(state, fitness);
        ind.evaluated = true;
    } // evaluate

    @Override
    public void describe(final EvolutionState state, final Elite ind, final int submap, final int threadnum, final int log) {
        super.describe(state, ind, submap, threadnum, log);
    }
}