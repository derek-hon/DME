package ec.distributedME;

import java.util.ArrayList;

import ec.Breeder;
import ec.Population;
import ec.EvolutionState;
import ec.util.Parameter;

/**
 * DistributedMEBreeder.java
 * 
 * @see ec.Breeder
 * @see ec.steadystate.SteadyStateBreeder
 * Repurposes the breeder to be compliant for Distributed MAP-Elites.
 * Utilizes SteadyStateBreeder as the core as it is conceptually similar.
 *  
 * Created: 25 January, 2022
 * @author Derek Hon
 * @version 1.0
 */

public class DistributedMEBreeder extends Breeder {

    private static final long serialVersionUID = 1;

    MapElitesBreedingSource[] bp;

    public DistributedMEBreeder() {
        bp = null;
    }

    public void setup(final EvolutionState state, final Parameter base) {
        state.output.exitIfErrors();
    }

    /**
     * Called to check to see if the breeding sources are correct -- if you use this
     * method, you must call state.output.exitIfErrors() immediately afterwards.
     */
    public void sourcesAreProperForm(final DistributedMEEvolutionState state,
            final MapElitesBreedingSource[] breedingSources) {
        for (int x = 0; x < breedingSources.length; x++) {
            //Make sure all sources are of MapElitesSourceForm
            if (breedingSources[x] != null)
                ((MapElitesSourceForm) (breedingSources[x])).sourcesAreProperForm(state);
        }
    }

    /**
     * Called whenever individuals have been replaced by new individuals in the
     * population.
     */
    public void individualReplaced(final EvolutionState state, final int submap, final int thread,
            final int individual) {
        for (int x = 0; x < bp.length; x++)
            ((MapElitesSourceForm) bp[x]).individualReplaced(state, submap, thread, individual);
    }

    public void finishPipelines(EvolutionState state) {
        if (bp[0] != null) {
            bp[0].finishProducing(state, 0, 0);
        }
    }

    public void prepareToBreed(EvolutionState state, int thread) {
        final DistributedMEEvolutionState DMEstate = (DistributedMEEvolutionState) state;
        // set up the breeding pipelines
        bp = new MapElitesBreedingSource[DMEstate.totalSubmaps];

        for (int submap = 0; submap < bp.length; submap++) {
            // if (DMEstate.distributedMap.submaps.get(submap).species.pipe_prototype != null) {
            if (DMEstate.distributedMap.subMAPs[submap].species.pipe_prototype != null) {
                // bp[submap] = (MapElitesBreedingSource) DMEstate.distributedMap.submaps.get(submap).species.pipe_prototype.clone();
                bp[submap] = (MapElitesBreedingSource) DMEstate.distributedMap.subMAPs[submap].species.pipe_prototype.clone();
                
                // if (!bp[submap].produces(state, DMEstate.distributedMap.submaps.get(submap), 0))
                    // DMEstate.output.error("The Breeding Source of submap " + submap
                    //     + " does not produce individuals of the expected species "
                    //     + DMEstate.distributedMap.submaps.get(submap).species.getClass().getName() + " and with the expected Fitness class "
                    //     + DMEstate.distributedMap.submaps.get(submap).species.f_prototype.getClass().getName());
                if (!bp[submap].produces(state, DMEstate.distributedMap.subMAPs[submap], 0))
                    DMEstate.output.error("The Breeding Source of submap " + submap
                            + " does not produce individuals of the expected species "
                            + DMEstate.distributedMap.subMAPs[submap].species.getClass().getName() + " and with the expected Fitness class "
                            + DMEstate.distributedMap.subMAPs[submap].species.f_prototype.getClass().getName());
                bp[submap].fillStubs(((DistributedMEEvolutionState) state), null);
            }
        }
        // are they of the proper form?
        sourcesAreProperForm(DMEstate, bp);
        // because I promised when calling sourcesAreProperForm
        DMEstate.output.exitIfErrors();

        // warm them up
        for (int i = 0; i < bp.length; i++) {
            if (bp[i] != null) {
                bp[i].prepareToProduce(((DistributedMEEvolutionState) state), i, 0);
            }
        }
    }

    public Elite breedIndividual(final DistributedMEEvolutionState state, int submap, int thread) {
        ArrayList<Elite> newind = new ArrayList<Elite>();

        // breed a single individual
        bp[0].produce(submap, newind, ((DistributedMEEvolutionState) state), thread,
                ((DistributedMEEvolutionState) state).distributedMap.subMAPs[submap].species.buildMisc(state, submap, thread));
        // bp[0].produce(submap, newind, ((DistributedMEEvolutionState) state), thread,
        //         ((DistributedMEEvolutionState) state).distributedMap.submaps.get(submap).species.buildMisc(state, submap, thread));
        return (Elite) newind.get(0).clone();
    }

    /** Empty because we have no need for it */
    public Population breedPopulation(EvolutionState state) {
        return null;
    }
}
