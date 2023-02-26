package ec.app.DMEtutorial;

import ec.util.Parameter;
import ec.gp.koza.*;
import ec.distributedME.*;
import ec.EvolutionState;
import ec.app.DMEtutorial.Fitness.Haar;
import ec.app.DMEtutorial.Fitness.Haar.ImageQueryData;;
/**
 * ProceduralTextureFitness.java Created: 05/05/2020 By: Derek Hon
 **/

@SuppressWarnings("serial")
public class ProceduralTextureWavelet extends ImageLoad {

    double[] TargetSample;
    Haar Target = null;
    ImageQueryData solutionHaar = null;
    int width = 0, height = 0;
    ImageQueryData TargetData;
    String savePath = "";

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        savePath = state.parameters.getString(base.push(P_SAVE_PATH), null);

        width = TargetImage.getWidth();
        height = TargetImage.getHeight();

        Target = new Haar(state, TargetImage);
        TargetData = Target.getHaarData();
    }

    @Override
    public void evaluate(EvolutionState state, Elite ind, int submap, int threadnum) {
        if(ind.evaluated) return;
        super.evaluate(state, ind, submap, threadnum);

        Haar solution = new Haar(state, ind);
        solutionHaar = solution.getHaarData();
        double fitness = TargetData.compareTo(solutionHaar);
        // state.output.message(fitness + "");

        KozaFitness f = ((KozaFitness)ind.fitness);
        f.setStandardizedFitness(state, fitness);
        ind.evaluated = true;
    } // evaluate

    @Override
    public void describe(final EvolutionState state, final Elite ind, final int submap, final int threadnum, final int log) {
        super.describe(state, ind, submap, threadnum, log);
    }
}