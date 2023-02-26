package ec.distributedME;


import java.util.ArrayList;
import java.util.Arrays;

import ec.EvolutionState;
import ec.Exchanger;
import ec.gp.GPIndividual;
import ec.gp.GPTree;
import ec.gp.koza.KozaFitness;
import ec.thesisStatistics.BaselineMEStatistics;
import ec.util.Parameter;

/**
 * DistributedMEEvolutionState.java
 * 
 * @see ec.EvolutionState
 * Repurposes the EvolutionState for MAP-Elites.
 * 
 * 
 * Created: 10 February, 2022
 * @author Derek Hon
 * @version 1.0
 */
public class DistributedMEEvolutionState extends EvolutionState {

    /** Current number of evaluations which have occurred within the run. */
    public int currentEvals;

    /** Current distributed map, should be accessed in a read only fashion. */
    public DistributedMap distributedMap;

    /** First time calling evolve. */
    protected boolean firstTime;

    /** Holds which subpopulation we are currently operating on */
    int whichSubmap;

    /** How many individuals have we added to the initial map? */
    int individualCount;

    /** Total number of islands */
    int totalSubmaps;

    /** Percentage of the total map size to fill. */
    double mapPercent;

    /** The behaviour evaluations for each island */
    public BehaviourEvaluation[] behaviourEvaluations;

    /** Transactions made within the MAP */
    // HashMap<String, Integer> transactions;

    /**
     * When a new individual arrives, with what probability should it directly
     * replace the existing "marked for death" individual, as opposed to only
     * replacing it if it's superior? Only considered when two individuals have the same fitness.
     */
    public double replacementProbability;

    /** MAP exchanger, a singleton object. You should only access in a read-only fashion. */
    DistributedMEExchanger exchanger;

    public static final String P_STARTING_INDS           = "starting-individuals";
    public static final String P_REPLACEMENT_PROBABILITY = "replacement-probability";
    public static final String P_TOTAL_EVAL              = "total-evaluations";
    public static final String P_SUBMAP_SIZE             = "submaps";
    public static final String P_BEHAVIOUR_SOURCE        = "behaviour-source";
    public static final String P_MAP                     = "map";
    public static final String P_SUBMAP                  = "submap";

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        // transactions = new HashMap<String, Integer>();

        // double check that we have valid evaluators and breeders and exchangers
        if (!(breeder instanceof DistributedMEBreeder))
            state.output.error(
                    "You've chosen to use Distributed Map-Elites Evolution, but your breeder is not of the class Map-ElitesBreeder.",
                    base);
        if (!(evaluator instanceof MapElitesEvaluator))
            state.output.error(
                    "You've chosen to use Distributed Map-Elites Evolution, but your evaluator is not of the class Map-ElitesEvaluator.",
                    base);
            

        Parameter p;
        Parameter map = new Parameter(P_MAP);

        /** Set up exchanger */
        p = new Parameter(P_EXCHANGER);
        exchanger = (DistributedMEExchanger) (parameters.getInstanceForParameter(p, null, Exchanger.class));
        exchanger.setup(this, p);
        if (parameters.exists(MapElitesDefaults.base().push(P_STARTING_INDS), null)) {
            mapPercent = parameters.getDouble(MapElitesDefaults.base().push(P_STARTING_INDS), null);
            if (mapPercent < 0.0) // uh oh
                state.output.error("Starting Individual Percent must be between 0.0 and 1.0 inclusive.",
                        MapElitesDefaults.base().push(P_STARTING_INDS), null);
            // state.output.message("Starting individuals will try to be " + mapPercent + "% of the maps. Hard cutoff at 50000 evaluations.");
        } else {
            mapPercent = 0.5;
            state.output.message("Starting individuals not defined: using 50% of the map size.");
        }
        if (parameters.exists(MapElitesDefaults.base().push(P_REPLACEMENT_PROBABILITY), null)) {
            replacementProbability = parameters
                    .getDoubleWithMax(MapElitesDefaults.base().push(P_REPLACEMENT_PROBABILITY), null, 0.0, 1.0);
            if (replacementProbability < 0.0) // uh oh
                state.output.error("Replacement probability must be between 0.0 and 1.0 inclusive.",
                        MapElitesDefaults.base().push(P_REPLACEMENT_PROBABILITY), null);
        } else {
            replacementProbability = 1.0; // always replace
            state.output.message("Replacement probability not defined: using 1.0 (always replace)");
        }

        if (parameters.exists(map.push(P_SUBMAP_SIZE), null)) {
            totalSubmaps = parameters.getInt(map.push(P_SUBMAP_SIZE), null);
            if (totalSubmaps < 0)
                output.error("Submaps must be greater than 0");
            behaviourEvaluations = new BehaviourEvaluation[totalSubmaps];
            Parameter submap;
            for (int i = 0 ; i < totalSubmaps ; i ++) {
                submap = map.push(P_SUBMAP).push("" + i);
                behaviourEvaluations[i] = (BehaviourEvaluation) state.parameters.getInstanceForParameter(
                    submap.push(P_BEHAVIOUR_SOURCE), null, BehaviourEvaluation.class
                );
            }
        } else {
            state.output.error("No submap amount entered", MapElitesDefaults.base().push(P_SUBMAP_SIZE),
                    null);
        }
    }

    public void startFresh() {
        output.message("Setting up");
        setup(this, null);

        //MAP Initialization
        output.message("Initializing Evaluation 0...");
        ((DistributedMEStatistics) statistics).preInitializationStatistics(this);
        this.distributedMap = ((DistributedMEInitializer) initializer).initialMap(this, 0);
        //Initialize variables
        whichSubmap = -1;
        firstTime = true;
        evaluations = 0;
        
        // parseAllNames();
    }

    /**
     * Evolution process
     * 
     * Sets up the statistics, breeder, and evaluator if its the first evaluation.
     * Goes through each submap per evaluation.
     * 
     */
    public int evolve() {
        if (firstTime) {
            ((DistributedMEStatistics) statistics).postInitializationStatistics(this);
            ((DistributedMEBreeder) breeder).prepareToBreed(this, 0);
            ((MapElitesEvaluator) evaluator).prepareToEvaluate(this, 0);
            firstTime = false;
        }

        //round robin selection
        whichSubmap = (whichSubmap + 1) % totalSubmaps;

        // Main Evolution Loop
        if (((MapElitesEvaluator) evaluator).canEvaluate()) {
            Elite ind = (Elite) ((DistributedMEBreeder) breeder).breedIndividual(this, whichSubmap, 0).clone();
            evaluateIndividual(ind, whichSubmap);
            EliteMap eliteMap = this.distributedMap.getEliteMAP(whichSubmap);
            
            eliteMap.newEliteProcess(this, ind);
        }

        //Exchange operations
        // PRE-BREEDING EXCHANGING
        if (evaluations > 0) {
            ((DistributedMEExchanger) exchanger).preBreedingExchangeEliteMap(this);

            // POST-BREEDING EXCHANGE
            ((DistributedMEExchanger) exchanger).postBreedingExchangEliteMap(this);
        }

        if (evaluations >= numEvaluations) {
            ((DistributedMEStatistics) statistics).postEvaluationStatistics(this);
            return R_SUCCESS;
        }

        if (this.evaluations > 0)
            ((DistributedMEStatistics)statistics).postEvaluationStatistics(this);
        this.evaluations ++;
        return R_NOTDONE;
    }

    /**
     * Abstracted out of the evolution loop unlike SteadyState so that the exchanger can use this. Want exchanger to also evaluate
     * to avoid looping through the entire map and looking for the evaluated flag.
     * 
     * @see ec.steadystate.SteadyStateEvolutionState#evolve
     */
    protected void ProcessIndividual(Elite e, int submap) {
        EliteMap eliteMap = this.distributedMap.getEliteMAP(submap);
        
        eliteMap.newEliteProcess(this, e);
    }

    public void evaluateIndividual(Elite e, int submap) {
        ((MapElitesEvaluator) evaluator).evaluateIndividual(this, e, submap);
    }

    public void finish(int result) {
        output.message("Total Evaluations: " + evaluations);
        ((DistributedMEBreeder) breeder).finishPipelines(this);
        ((DistributedMEStatistics) statistics).finalStatistics(this, result);
        // finisher.finishPopulation(this, result);
    } // finish
}
