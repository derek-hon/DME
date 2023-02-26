package ec.distributedME;

import java.util.ArrayList;

import ec.EvolutionState;
import ec.Setup;
import ec.util.Parameter;

public class DistributedMap implements Cloneable, Setup{
    private static final long serialVersionUID = 1;

    /** Equivalent to subpopulations */
    public ArrayList<EliteMap> submaps               = new ArrayList<>();

    /** Collection of islands */
    public EliteMap[] subMAPs;

    //Parameters for ECJ
    public static final String P_SUBMAP_SIZE         = "submaps";
    public static final String P_SUBMAP              = "submap";
    public static final String P_BEHAVIOUR_SOURCE    = "behaviour-source";
    public static final String P_DEFAULT_SUBMAP      = "default-submap";
    public static final String NUM_SUBMAPS_PREAMBLE  = "Number of Submaps: ";
    public static final String SUBMAP_INDEX_PREAMBLE = "Submap Number: ";

    public DistributedMap emptyClone() {
        try {
            DistributedMap m = (DistributedMap) clone();
            m.subMAPs = new EliteMap[subMAPs.length];
            for (int i = 0 ; i < subMAPs.length ; i ++)
                m.subMAPs[i] = (EliteMap) subMAPs[i].emptyClone();
            return m;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();// never happens
        }
    }

    // public DistributedMap emptyClone() {
    //     try {
    //         DistributedMap m = (DistributedMap) clone();
    //         m.submaps = new ArrayList<EliteMap>(submaps);
    //         for (int i = 0 ; i < submaps.size() ; i ++)
    //             m.submaps.add((EliteMap) submaps.get(i).emptyClone());
    //         return m;
    //     } catch (CloneNotSupportedException e) {
    //         throw new InternalError();// never happens
    //     }
    // }

    public void clear() {
        for (int i = 0 ; i < submaps.size() ; i ++)
            ((EliteMap) submaps.get(i)).clearMap();
    }

    public void setup(final EvolutionState state, final Parameter base) {
        Parameter p;

        p = base.push(P_SUBMAP_SIZE);
        int size = state.parameters.getInt(p, null, 1);
        if (size <= 0)
            state.output.fatal("Submap count must be greater than 0.\n", base.push(P_SUBMAP_SIZE));
        // submaps = new ArrayList<EliteMap>(submaps.size());
        subMAPs = new EliteMap[size];
        
        //Set up submaps
        for (int i = 0 ; i < size ; i ++) {
            p = base.push(P_SUBMAP).push("" + i);

            if (!state.parameters.exists(p, null)) {
                p = base.push(P_DEFAULT_SUBMAP);
                int defaultSubmap = state.parameters.getInt(p, null, 0);
                if (defaultSubmap >= 0) {
                    state.output.warning("Using submap " + defaultSubmap + " as the default for submap " + i);
                    p = base.push(P_SUBMAP).push("" + defaultSubmap);
                }
            }
            subMAPs[i] = (EliteMap) (state.parameters.getInstanceForParameterEq(p, null, EliteMap.class));
            subMAPs[i].setup(state, p);

            // submaps.add((EliteMap) (state.parameters.getInstanceForParameterEq(p, null, EliteMap.class)));
            // submaps.get(i).setup(state, p);
        }
    }

    /** Populates the population with new individuals */
    public void populate(EvolutionState state, int thread) {
        for (int i = 0 ; i < subMAPs.length ; i ++) {
            subMAPs[i].populate(state, thread, i);

            // submaps.get(i).populate(state, thread, i);
            state.output.message(
                "Submap " + i + " successfully populated with: " + subMAPs[i].getOccupants() + " individuals.");
        }
    }

    public int getTotalSubMAPs() {
        return this.subMAPs.length;
    }

    public EliteMap getEliteMAP(int index) {
        return this.subMAPs[index];
    }
}
