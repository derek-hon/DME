package ec.distributedME;

import ec.*;
import ec.distributedME.*;
import ec.util.*;
import ec.select.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * MapElitesTournamentSelection.java
 * @ec.select.TournamentSelection
 * 
 * Created: 25 January, 2022
 * @author Derek Hon
 * @version 1.0
 */
public class MapElitesTournamentSelection extends MapElitesSelectionMethod implements MapElitesSourceForm {
    /** default base */
    public static final String P_TOURNAMENT = "tournament";

    public static final String P_PICKWORST = "pick-worst";

    /** size parameter */
    public static final String P_SIZE = "size";

    /** Base size of the tournament; this may change. */
    public int size;

    /** Probablity of picking the size plus one */
    public double probabilityOfPickingSizePlusOne;

    /** Do we pick the worst instead of the best? */
    public boolean pickWorst;

    public Parameter defaultBase() {
        return SelectDefaults.base().push(P_TOURNAMENT);
    }

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        Parameter def = defaultBase();

        double val = state.parameters.getDouble(base.push(P_SIZE), def.push(P_SIZE), 1.0);
        if (val < 1.0)
            state.output.fatal("Tournament size must be >= 1.", base.push(P_SIZE), def.push(P_SIZE));
        else if (val == (int) val) // easy, it's just an integer
        {
            size = (int) val;
            probabilityOfPickingSizePlusOne = 0.0;
        } else {
            size = (int) Math.floor(val);
            probabilityOfPickingSizePlusOne = val - size; // for example, if we have 5.4, then the probability of
                                                          // picking *6* is 0.4
        }

        pickWorst = state.parameters.getBoolean(base.push(P_PICKWORST), def.push(P_PICKWORST), false);
    }

    /**
     * Returns a tournament size to use, at random, based on base size and
     * probability of picking the size plus one.
     */
    public int getTournamentSizeToUse(MersenneTwisterFast random) {
        double p = probabilityOfPickingSizePlusOne; // pulls us to under 35 bytes
        if (p == 0.0)
            return size;
        return size + (random.nextBoolean(p) ? 1 : 0);
    }

    /**
     * Produces the index of a (typically uniformly distributed) randomly chosen
     * individual to fill the tournament. <i>number</> is the position of the
     * individual in the tournament.
     */
    public int getRandomIndividual(int number, int submap, EvolutionState state, int thread) {
        return state.random[thread].nextInt(((DistributedMEEvolutionState) state).distributedMap.subMAPs[submap].getOccupants());
    }

    /**
     * Returns true if *first* is a better (fitter, whatever) individual than
     * *second*.
     */
    public boolean betterThan(Elite first, Elite second, int submap, EvolutionState state, int thread) {
        return first.fitness.betterThan(second.fitness);
    }

    public int produce(final int submap, final EvolutionState state, final int thread) {
        // pick size random individuals, then pick the best.
        EliteMap eMap = ((DistributedMEEvolutionState) state).distributedMap.subMAPs[submap];
        int best = getRandomIndividual(0, submap, state, thread);

        int s = getTournamentSizeToUse(state.random[thread]);

        if (pickWorst)
            for (int x = 1; x < s; x++) {
                int j = getRandomIndividual(x, submap, state, thread);
                if (!betterThan(eMap.getElite(j), eMap.getElite(best), submap, state, thread)) // j is at least as
                                                                                           // bad as best
                    best = j;
            }
        else
            for (int x = 1; x < s; x++) {
                int j = getRandomIndividual(x, submap, state, thread);
                if (betterThan(eMap.getElite(j), eMap.getElite(best), submap, state, thread)) // j is better than
                                                                                          // best
                    best = j;
            }

        return best;
    }

    public void individualReplaced(final EvolutionState state, final int submap, final int thread,
            final int individual) {
        return;
    }

    public void sourcesAreProperForm(final EvolutionState state) {
        return;
    }
}
