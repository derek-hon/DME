package ec.distributedME;

import ec.util.Parameter;
import ec.gp.GPInitializer;
import ec.EvolutionState;
/*
 * DistributedMEInitializer.java
 *
 * Created: 19 May, 2020
 * By: Derek Hon
 */

/**
 * DistributedMEInitializer.java
 * 
 * @see ec.gp.GPInitializer
 * Responsible for helping populate the MAP as
 * well as setting up all the parameters around it.
 * 
 * 
 * Created: 10 February, 2022
 * @author Derek Hon
 * @version 1.0
 */
public class DistributedMEInitializer extends GPInitializer {
    private static final long serialVersionUID = 1;

    public static final String P_MAP = "map";

    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);
    }

    public DistributedMap initialMap(final EvolutionState state, int thread) {
        DistributedMap m = setupMap(state, thread);
        m.populate(state, thread);
        return m;
    }

    public DistributedMap setupMap(final EvolutionState state, int thread) {
        DistributedMap m;
        Parameter base = new Parameter(P_MAP);
        m = new DistributedMap();
        state.output.message("Setting up distributed map...");
        m.setup(state, base);
        state.output.message("Successfully set up distributed map.");
        return m;
    }
}