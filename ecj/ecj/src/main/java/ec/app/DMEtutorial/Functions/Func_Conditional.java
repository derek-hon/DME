package ec.app.DMEtutorial.Functions;

import ec.*;
import ec.gp.*;
import ec.app.experiments.GeneralandFitness.MultiData;

/**
 * Credit: Michael Gircys
 */
@SuppressWarnings("serial")
public abstract class Func_Conditional extends GPNode implements SimplifyableGPNode
{
	
	@Override 
	public boolean IsVolatile(){ return false; }
	@Override 
	public GPNode  GetSimplifiedReplacement(EvolutionState state)
	{ 
		return SimplifyableGPNode.SimplifyableGPNodeImpl.GetSimplifiedReplacement(state, this); 
	}
	
	// -- double input, double output -----------------------------------------

	// If C1 Greater-Than C2
	public static class IfGT extends Func_Conditional
	{
	    public String toString()         { return "IfGT"; }	    
	    public int    expectedChildren() { return 4; }	
	    public void eval(
	    	final EvolutionState state,
	        final int            thread,
	        final GPData         input,
	        final ADFStack       stack,
	        final GPIndividual   individual,
	        final Problem        problem
	    )
	    {
	        double c1, c2, val;
	        MultiData rd = ((MultiData)(input));
	
	        children[0].eval(state,thread,input,stack,individual,problem);
	        c1 = rd.GetD();
	        children[1].eval(state,thread,input,stack,individual,problem);
			c2 = rd.GetD();
			
			children[ (c1>c2)? 2 : 3 ].eval(state,thread,input,stack,individual,problem);
			val = rd.GetD();
			
	        rd.SetTo(  val  );
	    }	    
	}
}
	