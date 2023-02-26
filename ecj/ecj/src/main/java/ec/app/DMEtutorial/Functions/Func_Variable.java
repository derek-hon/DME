package ec.app.DMEtutorial.Functions;

import static ec.app.DMEtutorial.CoordinateVariables.*;

import ec.EvolutionState;
import ec.Problem;
import ec.app.DMEtutorial.MultiData;
import ec.app.DMEtutorial.TextureProblemForm;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.gp.SimplifyableGPNode;
import ec.util.Parameter;

/**
 * Credit: Michael Gircys
 */
@SuppressWarnings("serial")
public abstract class Func_Variable extends GPNode implements SimplifyableGPNode {
	@Override
	public int expectedChildren() {
		return 0;
	}

	@Override
	public boolean IsVolatile() {
		return true;
	}

	@Override
	public GPNode GetSimplifiedReplacement(EvolutionState state) {
		return this;
	}

	public static class Pos_X extends Func_Variable
	{
	    public String toString() { return "X"; }
	    public void eval(
	    	final EvolutionState state,
	        final int            thread,
	        final GPData         input,
	        final ADFStack       stack,
	        final GPIndividual   individual,
	        final Problem        problem
	    )
	    {
	        MultiData rd = ((MultiData)(input));
	        rd.SetTo(((TextureProblemForm) problem).Get_Current_Pos()[X.ordinal()]);
	    }
	}
	
	public static class Pos_Y extends Func_Variable
	{
	    public String toString() { return "Y"; }
	    public void eval(
	    	final EvolutionState state,
	        final int            thread,
	        final GPData         input,
	        final ADFStack       stack,
	        final GPIndividual   individual,
	        final Problem        problem
	    )
	    {
	        MultiData rd = ((MultiData)(input));
	        rd.SetTo(((TextureProblemForm) problem).Get_Current_Pos()[Y.ordinal()]);
	    }
	}

	// Polar radial coordinate
	public static class Pos_Rho extends Func_Variable
	{
	    public String toString() { return "Rho"; }
	    public void eval(
	    	final EvolutionState state,
	        final int            thread,
	        final GPData         input,
	        final ADFStack       stack,
	        final GPIndividual   individual,
	        final Problem        problem
	    )
	    {
	        MultiData rd = ((MultiData)(input));
	        rd.SetTo(  ((TextureProblemForm)problem).Get_Current_Pos()[Rho.ordinal()]  );
	    }
	}
}
