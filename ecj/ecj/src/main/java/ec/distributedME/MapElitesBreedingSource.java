/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.distributedME;

import ec.util.*;
import ec.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * MapElitesBreedingSource.java
 * @see ec.BreedingSource
 * 
 * Extends Breeding source and only changes two methods due to different
 * evolution states and population being a map. Took out beginning parameters in
 * produce because we will always only be creating a single individual.
 * 
 * 
 * Created: 25 January, 2022
 * 
 * @author Derek Hon
 * @version 1.0
 */

public abstract class MapElitesBreedingSource implements Prototype, RandomChoiceChooserD {

    public static final String P_PROB = "prob";
    public static final double NO_PROBABILITY = -1.0;

    public double probability;

    public void setup(final EvolutionState state, final Parameter base) {
        Parameter def = defaultBase();

        if (!state.parameters.exists(base.push(P_PROB), def.push(P_PROB)))
            probability = NO_PROBABILITY;
        else {
            probability = state.parameters.getDouble(base.push(P_PROB), def.push(P_PROB), 0.0);
            if (probability < 0.0)
                state.output.error(
                        "Breeding Source's probability must be a double floating point value >= 0.0, or empty, which represents NO_PROBABILITY.",
                        base.push(P_PROB), def.push(P_PROB));
        }
    }

    public abstract void prepareToProduce(EvolutionState state, int submap, int thread);

    public abstract void finishProducing(EvolutionState state, int submap, int thread);

    public abstract boolean produces(final EvolutionState state, final EliteMap newMap, int thread);

    public abstract int produce(int submap, ArrayList<Elite> inds, EvolutionState state, int thread,
            HashMap<String, Object> misc);

    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            throw new InternalError();
        } // never happens
    }

    public void fillStubs(final EvolutionState state, MapElitesBreedingSource source) {
    }

    /** Below methods are copied from ec.BreedingSource */

    public final double getProbability(final Object obj) {
        return ((MapElitesBreedingSource) obj).probability;
    }

    public final void setProbability(final Object obj, final double prob) {
        ((MapElitesBreedingSource) obj).probability = prob;
    }

    public static int pickRandom(final MapElitesBreedingSource[] sources, final double prob) {
        return RandomChoice.pickFromDistribution(sources, sources[0], prob);
    }

    public abstract int typicalIndsProduced();

    public static void setupProbabilities(final MapElitesBreedingSource[] sources) {
        RandomChoice.organizeDistribution(sources, sources[0], true);
    }

    public void preparePipeline(final Object hook) {
        // the default method does nothing, though BreedingPipelines override this
        // to guarantee that it's called on all their sources as well.
    }
}
