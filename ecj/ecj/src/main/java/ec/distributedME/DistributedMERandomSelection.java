package ec.distributedME;

import ec.EvolutionState;
import ec.util.Parameter;
import ec.util.IntBag;

import ec.select.SelectDefaults;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * DistributedMERandomSelection.java
 * 
 * @see ec.select.RandomSelection
 * Repurposes Random Selection to be compliant for my classes.
 * 
 * Modified: 25 January, 2022
 * @author Derek Hon
 * @version 1.0
 */

public class DistributedMERandomSelection  extends MapElitesSelectionMethod implements MapElitesSourceForm {
    /** default base */
    public static final String P_RANDOM = "random";

    public Parameter defaultBase() {
        return SelectDefaults.base().push(P_RANDOM);
    }

    public int produce(final int submap, final EvolutionState state, final int thread) {
        return ((DistributedMEEvolutionState) state).random[thread]
                .nextInt(((DistributedMEEvolutionState) state).distributedMap.subMAPs[submap].getOccupants());
        // return ((DistributedMEEvolutionState) state).random[thread]
        //         .nextInt(((DistributedMEEvolutionState) state).distributedMap.submaps.get(submap).Map.size());
    }

    // I hard-code both produce(...) methods for efficiency's sake

    public int produce(int submap, ArrayList<Elite> inds, EvolutionState state, int thread,
            HashMap<String, Object> misc) {
        int n = 1;
        EliteMap eliteMap = ((DistributedMEEvolutionState) state).distributedMap.subMAPs[submap];

        for (int i = 0 ; i < 2 ; i ++) {
            int index = state.random[thread].nextInt(eliteMap.getOccupants());
            
            inds.add(eliteMap.getElite(index));
            if (misc != null && misc.get(KEY_PARENTS) != null) {
                IntBag parent = new IntBag(1);
                parent.add(index);
                ((IntBag[]) misc.get(KEY_PARENTS))[0] = parent;
            }
        }
        return n;
    }

    public void individualReplaced(final EvolutionState state, final int thread, final int submap,
            final int individual) {
        return;
    }

    public void sourcesAreProperForm(final EvolutionState state) {
        return;
    }
}
