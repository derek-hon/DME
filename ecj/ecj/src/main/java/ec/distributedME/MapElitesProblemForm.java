/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.distributedME;

import ec.EvolutionState;

/**
 * MapElitesProblemForm.java
 *
 * @see ec.simple.SimpleProblemForm
 * 
 * Created: 25 January, 2022
 * 
 * @author Derek Hon
 * @version 1.0
 */

public interface MapElitesProblemForm {
  /**
   * Evaluates the individual in ind, if necessary (perhaps not evaluating them if
   * their evaluated flags are true), and sets their fitness appropriately.
   */

  public void evaluate(final EvolutionState state, final Elite ind, final int submap, final int threadnum);

  /**
   * "Reevaluates" an individual, for the purpose of printing out interesting
   * facts about the individual in the context of the Problem, and logs the
   * results. This might be called to print out facts about the best individual in
   * the population, for example.
   */

  public void describe(final EvolutionState state, final Elite ind, final int submap, final int threadnum, final int log);
}
