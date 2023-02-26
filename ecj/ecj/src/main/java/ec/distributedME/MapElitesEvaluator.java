/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/



package ec.distributedME;

import ec.EvolutionState;
import ec.Evaluator;

import ec.util.Parameter;

import ec.eval.MasterProblem;

import java.util.LinkedList;

/**
 * MapElitesEvaluator.java
 * @see ec.steadystate.SteadyStateEvaluator
 * Uses the steady state evaluator as the basis for modification
 * 
 * 
 * Modified: 25 January, 2022
 * @author Derek Hon
 * @version 1.0
 */

public class MapElitesEvaluator extends Evaluator
{
    LinkedList<QueueIndividual> queue = new LinkedList<QueueIndividual>();

    /** Sub-map currently being evaluated */
    int submapEvaluated = -1;

    /** Problem */
    MapElitesProblemForm problem;

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        

        if (!(p_problem instanceof MapElitesProblemForm))
            state.output.fatal("" + this.getClass() + " used, but the Problem is not of MapElitesProblemForm",
                    base.push(P_PROBLEM));
    }

    public void prepareToEvaluate(EvolutionState state, int thread) {
        problem = (MapElitesProblemForm) p_problem.clone();

        /*
           We only call prepareToEvaluate during Asynchronous Evolution.
        */
        if (problem instanceof MasterProblem)
            ((MasterProblem)problem).prepareToEvaluate(state, thread);
    }

    /** Submits an individual to be evaluated by the Problem, and adds it is added to the queue. */
    public void evaluateIndividual(final EvolutionState state, Elite ind, int submap) {
        prepareToEvaluate(state, submap);
        problem.evaluate(state, ind, submap, submap);
        queue.addLast(new QueueIndividual(ind, submap));
    }

    /** Returns true if we're ready to evaluate an individual.  Ordinarily this is ALWAYS true,
     except in the asynchronous evolution situation, where we may not have a processor ready yet. */
    public boolean canEvaluate() {
        if (problem instanceof MasterProblem)
            return ((MasterProblem)problem).canEvaluate();
        else return true;
    }

    /** Returns an evaluated individual is in the queue and ready to come back to us.
     Ordinarily this is ALWAYS true at the point that we call it, except in the asynchronous
     evolution situation, where we may not have a job completed yet, in which case NULL is
     returned. Once an individual is returned by this function, no other individual will
     be returned until the system is ready to provide us with another one.  NULL will
     be returned otherwise.  */
    public Elite getNextEvaluatedIndividual(EvolutionState state) {
        QueueIndividual qInd = null;

        qInd = (QueueIndividual)(queue.removeFirst());            
        
        submapEvaluated = qInd.submap;
        state.incrementEvaluations(1);
        return qInd.ind;
    }

    /** Returns the subpopulation of the last evaluated individual returned by getNextEvaluatedIndividual, or potentially -1 if
        getNextEvaluatedIndividual was never called or hasn't returned an individual yet. */
    public int getSubmapBeingEvaluated()
    {
        return submapEvaluated;
    }

    public int getQueueSize() {
        return this.queue.size();
    }

    /** Function here to satisfy abstract class */
    public void evaluatePopulation(final EvolutionState state) {}

    /** Run will only be complete after evaluation limit has been reached. */
    public String runComplete(final EvolutionState state) { return runComplete; } //runComplete
} //MapElitesEvaluator


