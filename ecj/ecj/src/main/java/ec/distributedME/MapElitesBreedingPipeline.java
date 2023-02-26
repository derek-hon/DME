/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.distributedME;

import ec.EvolutionState;
import ec.Individual;

import ec.util.Parameter;


/**
 * MapElitesBreedingPipeline.java
 *
 * @see ec.BreedingPipeline
 * Repurposes BreedingPipeline to be compliant with MAP-Elites.
 * Virtually the same as BreedingPipeline.
 * 
 * Created: 25 January, 2022
 * 
 * Combines Breeding Pipeline and GPBreedingPipeline
 * @author Derek Hon
 * @version 1.0
 */

public abstract class MapElitesBreedingPipeline extends MapElitesBreedingSource implements MapElitesSourceForm {
    /** Indicates that a source is the exact same source as the previous source. */
    public static final String V_SAME = "same";

    /**
     * Indicates the probability that the Breeding Pipeline will perform its
     * mutative action instead of just doing reproduction.
     */
    public static final String P_LIKELIHOOD = "likelihood";

    /**
     * Indicates that the number of sources is variable and determined by the user
     * in the parameter file.
     */

    public static final int DYNAMIC_SOURCES = -1;

    /**
     * Standard parameter for number of sources (only used if numSources returns
     * DYNAMIC_SOURCES
     */

    public static final String P_NUMSOURCES = "num-sources";

    /**
     * Standard parameter for individual-selectors associated with a
     * MapElitesBreedingPipeline
     */
    public static final String P_SOURCE = "source";

    /**
     * My parameter base -- I keep it around so I can print some messages that are
     * useful with it (not deep cloned)
     */

    public Parameter mybase;

    public double likelihood;

    /** Array of sources feeding the pipeline */
    public MapElitesBreedingSource[] sources;

    /**
     * Returns the number of sources to this pipeline. Called during
     * MapElitesBreedingPipeline's setup. Be sure to return a value > 0, or
     * DYNAMIC_SOURCES which indicates that setup should check the parameter file
     * for the parameter "num-sources" to make its determination.
     */

    public abstract int numSources();

    /**
     * Standard parameter for node-selectors associated with a GPBreedingPipeline
     */
    public static final String P_NODESELECTOR = "ns";

    /** Standard parameter for tree fixing */
    public static final String P_TREE = "tree";

    /** Standard value for an unfixed tree */
    public static final int TREE_UNFIXED = -1;

    /**
     * Returns the minimum among the typicalIndsProduced() for any children -- a
     * function that's useful internally, not very useful for you to call
     * externally.
     */
    public int minChildProduction() {
        if (sources.length == 0)
            return 0;
        int min = sources[0].typicalIndsProduced();
        for (int x = 1; x < sources.length; x++) {
            int cur = sources[x].typicalIndsProduced();
            if (min > cur)
                min = cur;
        }
        return min;
    }

    /**
     * Returns the maximum among the typicalIndsProduced() for any children -- a
     * function that's useful internally, not very useful for you to call
     * externally.
     */
    public int maxChildProduction() {
        if (sources.length == 0)
            return 0;
        int max = sources[0].typicalIndsProduced();
        for (int x = 1; x < sources.length; x++) {
            int cur = sources[x].typicalIndsProduced();
            if (max < cur)
                max = cur;
        }
        return max;
    }

    /**
     * Returns the "typical" number of individuals produced -- by default this is
     * the minimum typical number of individuals produced by any children sources of
     * the pipeline. If you'd prefer something different, override this method.
     */
    public int typicalIndsProduced() {
        return minChildProduction();
    }

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        mybase = base;
        Parameter def = defaultBase();

        likelihood = state.parameters.getDoubleWithDefault(base.push(P_LIKELIHOOD), def.push(P_LIKELIHOOD), 1.0);
        if (likelihood < 0.0 || likelihood > 1.0)
            state.output.fatal("Breeding Pipeline likelihood must be a value between 0.0 and 1.0 inclusive",
                    base.push(P_LIKELIHOOD), def.push(P_LIKELIHOOD));

        int numsources = numSources();
        if (numsources == DYNAMIC_SOURCES) {
            // figure it from the file
            numsources = state.parameters.getInt(base.push(P_NUMSOURCES), def.push(P_NUMSOURCES), 0);
            if (numsources == -1)
                state.output.fatal("Breeding pipeline num-sources value must exist and be >= 0",
                        base.push(P_NUMSOURCES), def.push(P_NUMSOURCES));
        }
        // it's negative
        else if (numsources <= DYNAMIC_SOURCES) {
            throw new RuntimeException("In " + this + " numSources() returned < DYNAMIC_SOURCES (that is, < -1)");
        } else {
            if (state.parameters.exists(base.push(P_NUMSOURCES), def.push(P_NUMSOURCES))) // uh oh
                state.output.warning(
                        "Breeding pipeline's number of sources is hard-coded to " + numsources
                                + " yet num-sources was provided: num-sources will be ignored.",
                        base.push(P_NUMSOURCES), def.push(P_NUMSOURCES));
        }

        sources = new MapElitesBreedingSource[numsources];

        for (int x = 0; x < sources.length; x++) {
            Parameter p = base.push(P_SOURCE).push("" + x);
            Parameter d = def.push(P_SOURCE).push("" + x);

            String s = state.parameters.getString(p, d);
            if (s != null && s.equals(V_SAME)) {
                if (x == 0) // oops
                    state.output.fatal("Source #0 cannot be declared with the value \"same\".", p, d);

                // else the source is the same source as before
                sources[x] = sources[x - 1];
            } // if
            else {
                sources[x] = (MapElitesBreedingSource) (state.parameters.getInstanceForParameter(p, d,
                        MapElitesBreedingSource.class));
                sources[x].setup(state, p);
            } // else

        } // for
        state.output.exitIfErrors();
    }

    public Object clone() {
        MapElitesBreedingPipeline c = (MapElitesBreedingPipeline) (super.clone());

        // make a new array
        c.sources = new MapElitesBreedingSource[sources.length];

        // clone the sources -- we won't go through the hassle of
        // determining if we have a DAG or not -- we'll just clone
        // it out to a tree. I doubt it's worth it.

        for (int x = 0; x < sources.length; x++) {
            if (x == 0 || sources[x] != sources[x - 1])
                c.sources[x] = (MapElitesBreedingSource) (sources[x].clone());
            else
                c.sources[x] = c.sources[x - 1];
        }

        return c;
    }

    /** Will not be using reproduce, do not want individuals being copied */
    public int reproduce(final int n, final int start, final Individual[] inds, final EvolutionState state,
            final int thread, boolean produceChildrenFromSource) {
        return 0;
    }

    public boolean produces(final EvolutionState state, final EliteMap newMap, int thread) {

        for (int x = 0; x < sources.length; x++) {
            if (!sources[x].produces(state, newMap, thread))
                return false;
        }
        if (newMap.species instanceof MapElitesGPSpecies)
            return true;
        return false;
    }

    public void prepareToProduce(final EvolutionState state, int submap, final int thread) {
        for (int x = 0; x < sources.length; x++)
            if (x == 0 || sources[x] != sources[x - 1])
                sources[x].prepareToProduce(state, submap, thread);
    }

    public void finishProducing(final EvolutionState state, final int submap, final int thread) {
        for (int x = 0; x < sources.length; x++)
            if (x == 0 || sources[x] != sources[x - 1])
                sources[x].finishProducing(state, submap, thread);
    }

    public void sourcesAreProperForm(final EvolutionState state) {
        for (int x = 0; x < sources.length; x++)
            if (!(sources[x] instanceof MapElitesSourceForm)) {
                state.output.error("The following breeding source is not of MapElitesSourceForm.",
                        mybase.push(P_SOURCE).push("" + x), defaultBase().push(P_SOURCE).push("" + x));
            } else
                ((MapElitesSourceForm) (sources[x])).sourcesAreProperForm(state);
    }

    public void individualReplaced(final EvolutionState state, final int submap, final int thread,
            final int individual) {
        for (int x = 0; x < sources.length; x++)
            ((MapElitesSourceForm) (sources[x])).individualReplaced(state, submap, thread, individual);
    }
}
