package ec.distributedME;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import ec.EvolutionState;
import ec.Statistics;
import ec.app.majority.func.E;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

/**
 * MapElitesStatistics.java
 * @see ec.simple.SimpleStatistics
 *
 * 
 * Created: 25 January, 2022
 * @author Derek Hon 
 * @version 1.0
 */

public class DistributedMEStatistics extends Statistics {
    public static final String P_DO_DEPTH           = "do-depth";
    public static final String P_STATISTICS_MODULUS = "modulus";
    public static final String P_STATISTICS_FILE    = "file";
    public static final String P_COMPRESS           = "gzip";
    public static final String P_DO_FINAL           = "do-final";
    public static final String P_DO_DESCRIPTION     = "do-description";
    public static final String P_SAVE_ALL           = "saveAll";
    public static final String P_SAVE_FITTEST       = "save-fittest";
    public static final String P_FITTEST            = "fittest-amount";
    public static final String P_SAVE_PATH          = "save-path";
    public static final String P_MAP_NAME           = "map-name";
    public static final String P_DO_HEADER          = "do-header";
    public static final String P_DO_TIME            = "do-time";
    public static final String P_DELIMITER          = "delimiter";
    public static final String P_DO_SIZE            = "do-size";
    public static final String P_DO_EVALUATION      = "do-evaluation";
    public static final String P_DO_MESSAGE         = "do-message";

    public boolean doSize;
    public String savePath;
    public String mapName;
    public String delimiter = " ";

    public boolean doDepth;
    public boolean doGraph;
    public boolean doFinal;
    public boolean doDescription;
    public boolean saveAll;
    public boolean saveFit;
    public boolean doHeader;
    public boolean doTime;
    public boolean doEvaluation;
    public boolean doMessage;

    public int fitAmount;
    public int modulus;
    public int statisticslog = 0; // stdout by default

    long[] totalDepthSoFarTree;
    long[] totalSizeSoFarTree;
    long totalIndsSoFar;

    public Elite[] bestOfRun;
    public long totalAssessed;
    public double totalFitness;
    public double prevMin = 10000000;

    // timings
    public long lastTime;

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        File statisticsFile = state.parameters.getFile(base.push(P_STATISTICS_FILE), null);

        modulus = state.parameters.getIntWithDefault(base.push(P_STATISTICS_MODULUS), null, 1);

        if (statisticsFile != null) {
            try {
                statisticslog = state.output.addLog(statisticsFile,
                        !state.parameters.getBoolean(base.push(P_COMPRESS), null, false),
                        state.parameters.getBoolean(base.push(P_COMPRESS), null, false));
            } // try
            catch (IOException i) {
                state.output
                        .fatal("An IOException occurred while trying to create the log " + statisticsFile + ":\n" + i);
            } // catch
        } // if
        else
            state.output.warning("No statistics file specified, printing to stdout at end.",
                    base.push(P_STATISTICS_FILE));

        // state.output.message(base.push(P_DO_GRAPH).toString());

        doSize = state.parameters.getBoolean(base.push(P_DO_SIZE), null, false);
        saveAll = state.parameters.getBoolean(base.push(P_SAVE_ALL), null, true);
        saveFit = state.parameters.getBoolean(base.push(P_SAVE_FITTEST), null, false);
        doDepth = state.parameters.getBoolean(base.push(P_DO_DEPTH), null, false);
        // doGraph = state.parameters.getBoolean(base.push(P_DO_GRAPH), null, false);
        doFinal = state.parameters.getBoolean(base.push(P_DO_FINAL), null, true);
        doDescription = state.parameters.getBoolean(base.push(P_DO_DESCRIPTION), null, true);
        fitAmount = state.parameters.getInt(base.push(P_FITTEST), null, 1);
        savePath = state.parameters.getString(base.push(P_SAVE_PATH), null);
        mapName = state.parameters.getString(base.push(P_MAP_NAME), null);
        doHeader = state.parameters.getBoolean(base.push(P_DO_HEADER), null, true);
        doTime = state.parameters.getBoolean(base.push(P_DO_TIME), null, false);
        doEvaluation = state.parameters.getBoolean(base.push(P_DO_EVALUATION),null,true);
        doMessage = state.parameters.getBoolean(base.push(P_DO_MESSAGE),null,true);
    } // setup

    public String getHeader() {
        final StringBuilder sb = new StringBuilder("evaluation");
        if (doTime)
            sb.append(delimiter).append("time");
        if (doSize) {
            sb.append(delimiter).append("meanSizeThisEval");
            sb.append(delimiter).append("meanSizeSoFar");
            sb.append(delimiter).append("sizeOfBestOfEval");
            sb.append(delimiter).append("sizeOfBestSoFar");
        }
        sb.append(delimiter).append("meanFitness");
        sb.append(delimiter).append("bestOfEvalFitness");
        sb.append(delimiter).append("bestSoFarFitness");
        return sb.append("\n").toString();
    }

    public void preInitializationStatistics(final EvolutionState state) {
        super.preInitializationStatistics((EvolutionState) state);

        if (doHeader)
            state.output.print(getHeader(), statisticslog);
        boolean output = (state.evaluations % modulus == 0);

        if (output && doTime)
            lastTime = System.currentTimeMillis();
    }

    public void postInitializationStatistics(final EvolutionState state) {
        super.postInitializationStatistics(state);
        
        // set up our best_of_run array -- can't do this in setup, because
        // we don't know if the number of subpopulations has been determined yet
        bestOfRun = new Elite[((DistributedMEEvolutionState) state).distributedMap.subMAPs.length];
    }

    /** Logs the best individual of the generation. */
    boolean warned = false;

    public void postEvaluationStatistics(final EvolutionState state) {
        super.postEvaluationStatistics(state);
        
        boolean output = (state.evaluations % modulus == 0);
        // state.output.message(state.evaluations + " % " + modulus + " = " +
        // (state.evaluations % modulus));
        if (output) {
            state.output.message("Evaluations so far: " + state.evaluations);

            EliteMap eliteMap = null;

            // for now we just print the best fitness per subpopulation.
            Elite[] best_i = new Elite[((DistributedMEEvolutionState) state).distributedMap.subMAPs.length]; // quiets
                                                                                                             // compiler
                                                                                                             // complaints
            for (int x = 0; x < ((DistributedMEEvolutionState) state).distributedMap.subMAPs.length; x++) {
                eliteMap = ((DistributedMEEvolutionState) state).distributedMap.subMAPs[x];

                Elite firstElite = eliteMap.getElite(0);
                best_i[x] = firstElite;

                for (Elite e : eliteMap.asArray()) {
                    if (((KozaFitness) e.fitness).betterThan(best_i[x].fitness)) {
                        best_i[x] = e;
                    }

                    if (bestOfRun[x] == null || ((KozaFitness) best_i[x].fitness).betterThan(bestOfRun[x].fitness)) {
                        bestOfRun[x] = (Elite) best_i[x].clone();
                    }
                }
            }

            // print the best-of-generation individual
            if (doEvaluation)
                state.output.println("\nEvaluation: " + state.evaluations, statisticslog);
            for (int x = 0; x < ((DistributedMEEvolutionState) state).distributedMap.subMAPs.length; x++) {
                double totalFitness = 0.0;
                eliteMap = ((DistributedMEEvolutionState) state).distributedMap.subMAPs[x];

                for (Elite e : eliteMap.asArray()) {
                    totalFitness += ((KozaFitness) e.fitness).standardizedFitness();
                }

                if (doEvaluation) {
                    state.output.println("Submap " + x + " best fitness: " +
                            ((KozaFitness) best_i[x].fitness).standardizedFitness(), statisticslog);
                    state.output.println("Submap " + x + " mean fitness: " + (double) ((totalFitness)
                            / (double) ((DistributedMEEvolutionState) state).distributedMap.subMAPs[x].getOccupants()),
                            statisticslog);
                }

                if (doMessage && !silentPrint) {
                    state.output.message("Submap " + x + " best fitness: " +
                            ((KozaFitness) best_i[x].fitness).standardizedFitness());
                    state.output.message("Submap " + x + " mean fitness: " + (double) ((totalFitness)
                            / (double) ((DistributedMEEvolutionState) state).distributedMap.subMAPs[x].getOccupants()));
                }
            }
        }
    }

    /** Logs the best individual of the run. */
    public void finalStatistics(final EvolutionState state, final int result) {
        super.finalStatistics(state,result);

        EliteMap eliteMap = null;
        
        int total = 0, occupied = 0;
        for (int x = 0; x < ((DistributedMEEvolutionState) state).distributedMap.subMAPs.length; x++) {
            eliteMap = ((DistributedMEEvolutionState) state).distributedMap.subMAPs[x];
            total += eliteMap.getTotalSize();
            occupied += eliteMap.getOccupants();
        }

        state.output.println("\nIndividuals being held within the MAP: " + occupied, statisticslog);
        state.output.println("Total individuals possible in MAP: " + total, statisticslog);
        state.output.println("Ratio of individuals in MAP: " + (double) (occupied / total), statisticslog);

        // if (doTime) {
        //     state.output.println("\nTotal time:" + delimiter + (System.currentTimeMillis() - beginTime), statisticslog);
        // }

        if (doFinal)
            state.output.println("\nBest Individual of Run:", statisticslog);

        for (int x = 0; x < ((DistributedMEEvolutionState) state).distributedMap.subMAPs.length; x++) {
            if (doFinal)
                state.output.println("Submap " + x + ":", statisticslog);
            if (doFinal)
                bestOfRun[x].printIndividualForHumans(state, statisticslog);
            if (doMessage && !silentPrint)
                state.output.message("Submap " + x + " best fitness of run: " + bestOfRun[x].fitness.fitnessToStringForHumans());

            
            eliteMap = ((DistributedMEEvolutionState) state).distributedMap.subMAPs[x];

            for (Elite e : eliteMap.asArray()) {
                state.output.println("Behaviour fitness" + e.getKey() + ": " + e.getRunningFitness() / e.getReplacementCounter(), statisticslog);
                state.output.println("Behaviour replacement" + e.getKey() + ": " + e.getReplacementCounter(), statisticslog);
            }
            
            
            int avgBestTreeDepth = 0;
            for (int i = 0 ; i < bestOfRun[x].trees.length ; i ++) {
                avgBestTreeDepth += bestOfRun[x].trees[i].child.depth();
            }
            avgBestTreeDepth /= bestOfRun[x].trees.length;
            state.output.println("Submap " + x + " best average depth: " + avgBestTreeDepth, statisticslog);

            // finally describe the winner if there is a description
            if (doFinal && doDescription)
                if (state.evaluator.p_problem instanceof MapElitesProblemForm)
                    ((MapElitesProblemForm) (state.evaluator.p_problem.clone())).describe(state, bestOfRun[x], x, 0,
                            statisticslog);
        }
    }
}
