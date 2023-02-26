/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.distributedME;

import ec.Problem;
import ec.EvolutionState;

import ec.util.Parameter;

import ec.gp.GPData;
import ec.gp.GPDefaults;
import ec.gp.ADFStack;
/**
 * MapElitesProblem.java
 * 
 * @see  ec.gp.GPProblem
 * 
 * Created: 25 January, 2022
 * @author Derek Hon
 * @version 1.0
 */

public abstract class MapElitesProblem extends Problem implements MapElitesProblemForm {
    public final static String P_GPPROBLEM = "problem";
    public final static String P_STACK = "stack";
    public final static String P_DATA = "data";

    /** The GPProblem's stack */
    public ADFStack stack;

    /** The GPProblem's GPData */
    public GPData input;

    /**
     * GPProblem defines a default base so your subclass doesn't
     * absolutely have to.
     */
    public Parameter defaultBase() {
        return GPDefaults.base().push(P_GPPROBLEM);
    }

    public void setup(final EvolutionState state, final Parameter base) {
        Parameter p = base.push(P_STACK);
        Parameter def = defaultBase();

        stack = (ADFStack) (state.parameters.getInstanceForParameterEq(
                p, def.push(P_STACK), ADFStack.class));
        stack.setup(state, p);

        p = base.push(P_DATA);
        input = (GPData) (state.parameters.getInstanceForParameterEq(
                p, def.push(P_DATA), GPData.class));
        input.setup(state, p);
    }

    public Object clone() {
        MapElitesProblem prob = (MapElitesProblem) (super.clone());

        // deep-clone the stack; it's not shared
        prob.stack = (ADFStack) (stack.clone());

        // deep-clone the data
        prob.input = (GPData) (input.clone());

        return prob;
    }
}
