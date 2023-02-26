package ec.app.DMEtutorial.Functions;

import static ec.app.experiments.GeneralandFitness.CoordinateVariables.*;

import java.awt.image.BufferedImage;
import ec.*;
import ec.gp.*;
import ec.app.DMEtutorial.MultiData;
import ec.app.DMEtutorial.TextureProblemForm;

public abstract class Func_Geometric extends GPNode implements SimplifyableGPNode {
    @Override 
	public boolean IsVolatile(){ return true; }
	@Override 
	public GPNode  GetSimplifiedReplacement(EvolutionState state)
	{ 
		return SimplifyableGPNode.SimplifyableGPNodeImpl.GetSimplifiedReplacement(state, this); 
	} 
	
	public static class ShiftPos extends Func_Geometric
	{
		@Override public int expectedChildren() { return 3; }
		@Override public String toString() { return "Shift"; }
		@Override public void eval(
			final EvolutionState state,
	        final int            thread,
	        final GPData         input,
	        final ADFStack       stack,
	        final GPIndividual   individual,
	        final Problem        problem
	    )
		{
			TextureProblemForm prob = ((TextureProblemForm)problem);
			MultiData rd = ((MultiData)(input));
			
			children[1].eval(state,thread,input,stack,individual,problem);
			double shiftX = rd.GetD();
			children[2].eval(state,thread,input,stack,individual,problem);
			double shiftY = rd.GetD();
			
			double[] orig_pos = prob.Get_Current_Pos();
			double[] new_pos = new double[orig_pos.length];
			new_pos[X.ordinal()] = orig_pos[X.ordinal()] + shiftX;
			new_pos[Y.ordinal()] = orig_pos[Y.ordinal()] + shiftY;
			
			prob.Set_Current_Pos(new_pos);
			children[0].eval(state,thread,input,stack,individual,problem);
			prob.Set_Current_Pos(orig_pos);
		}
	}
	
	public static class Tile extends Func_Geometric
	{
	    public String toString()         { return "Tile"; }
	    public int    expectedChildren() { return 3;      }
	    public void eval(
	    	final EvolutionState state,
	        final int            thread,
	        final GPData         input,
	        final ADFStack       stack,
	        final GPIndividual   individual,
	        final Problem        problem
	    )
	    {   
	    	TextureProblemForm prob = ((TextureProblemForm)problem);
	        MultiData rd = ((MultiData)(input));

	        double[] orig_pos = prob.Get_Current_Pos();
			double[] new_pos = new double[orig_pos.length];
	        
	        children[1].eval(state,thread,input,stack,individual,problem);
	        int tilesX = (int) Math.max(1,Math.ceil( rd.GetD() )); // get tile count
	        // [-1,1] extents of range has difference of 2.
	        // Speed up the (tile's) traversal (in relation to the canvas) by the number of tiles
	        new_pos[X.ordinal()] = (orig_pos[X.ordinal()]+1)/2;
	        new_pos[X.ordinal()] *= tilesX;
	        new_pos[X.ordinal()] -= (int)(new_pos[X.ordinal()]);
	        new_pos[X.ordinal()] = (new_pos[X.ordinal()]*2)-1;
	        
	        children[2].eval(state,thread,input,stack,individual,problem);
	        int tilesY = (int) Math.max(1,Math.ceil( rd.GetD() ));
	        new_pos[Y.ordinal()] = (orig_pos[Y.ordinal()]+1)/2;
	        new_pos[Y.ordinal()] *= tilesY;
	        new_pos[Y.ordinal()] -= (int)(new_pos[Y.ordinal()]);
	        new_pos[Y.ordinal()] = (new_pos[Y.ordinal()]*2)-1;
			
			
			prob.Set_Current_Pos(new_pos);
			children[0].eval(state,thread,input,stack,individual,problem);
			prob.Set_Current_Pos(orig_pos);
	    }
	}
}
