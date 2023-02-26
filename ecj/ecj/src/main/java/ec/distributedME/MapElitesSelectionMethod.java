/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.distributedME;

import ec.EvolutionState;
import ec.util.IntBag;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * MapElitesSelectionMethod.java
 *
 * @see ec.SelectionMethod
 * 
 * 
 * Created: 25 January, 2022
 * @author Derek Hon
 * @version 1.0
 */
public abstract class MapElitesSelectionMethod extends MapElitesBreedingSource {
    public static final int INDS_PRODUCED = 1;
    public static final String KEY_PARENTS = "parents";

    /** Returns 1 (the typical default value) */
    public int typicalIndsProduced() {
        return INDS_PRODUCED;
    }

    /**
     * A default version of produces -- this method always returns true under the
     * assumption that the selection method works with all Fitnesses. If this isn't
     * the case, you should override this to return your own assessment.
     */
    public boolean produces(final EvolutionState state, final EliteMap newmap, final int thread) {
        return true;
    }

    /** A default version of prepareToProduce which does nothing. */
    public void prepareToProduce(final EvolutionState s, final int submap, final int thread) {
        return;
    }

    /** A default version of finishProducing, which does nothing. */
    public void finishProducing(final EvolutionState s, final int submap, final int thread) {
        return;
    }

    public int produce(int submap, ArrayList<Elite> inds, EvolutionState state, int thread,
            HashMap<String, Object> misc) {
        int start = inds.size();

        int n = produceWithoutCloning(submap, inds, state, thread, misc);

        // clone every produced individual
        for (int q = start; q < n + start; q++) {
            // System.err.println("" + this + " makes " + inds.get(q));
            inds.set(q, (Elite) (inds.get(q).clone()));
        }

        return n;
    }

    public int produceWithoutCloning(final int submap, final ArrayList<Elite> inds, final EvolutionState state,
            final int thread, HashMap<String, Object> misc) {
        int start = inds.size();

        int n = INDS_PRODUCED;

        for (int q = 0; q < n; q++) {
            int index = produce(submap, inds, state, thread, misc);

            inds.add(((DistributedMEEvolutionState) state).distributedMap.subMAPs[submap].getElite(index));
            // by Ermo. seems the misc forget to check if misc is null
            if (misc != null && misc.get(KEY_PARENTS) != null) {
                IntBag bag = new IntBag(1);
                bag.add(index);
                ((IntBag[]) misc.get(KEY_PARENTS))[start + q] = bag;
            }
        }
        return n;
    }

    /** An alternative form of "produce" special to Selection Methods;
        selects an individual from the given subpopulation and 
        returns its position in that subpopulation. */
    public abstract int produce(final int submap,
        final EvolutionState state,
        final int thread);
}
