package ec.app.DMEtutorial.Functions;

import static ec.app.DMEtutorial.CoordinateVariables.*;

import ec.*;
import ec.gp.*;
import ec.util.Code;
import ec.util.DecodeReturn;
import ec.util.MersenneTwisterFast;
import ec.app.DMEtutorial.MultiData;
import ec.app.DMEtutorial.TextureProblemForm;

import java.awt.image.BufferedImage;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class Func_Noise extends ERC implements SimplifyableGPNode {
    public long seed;
	
	@Override public int expectedChildren() { return 0;   }
	@Override public String name()   { return "Noise?"; }
	@Override public String toStringForHumans() { return name() + "[" + String.valueOf(seed) + "]"; }
	@Override public String encode() { return Code.encode(seed); }
	@Override public boolean decode(DecodeReturn dret) 
	{
		// From the manual.
		int    pos 	= dret.pos;
		String data = dret.data;
		Code.decode(dret);
		if (dret.type != DecodeReturn.T_LONG)
		{ 
			dret.data = data; 
			dret.pos = pos; 
			return false; 
		}
		seed = dret.l;
		return true;
	}
	@Override public void readNode( EvolutionState state, DataInput input)   throws IOException { seed = input.readLong(); }
	@Override public void writeNode(EvolutionState state, DataOutput output) throws IOException { output.writeLong(seed);  }
	
	@Override public boolean nodeEquals(GPNode node) 
	{
		if(!(node instanceof Func_Noise)) return false;
		Func_Noise nnode = (Func_Noise)node;
		return nnode.seed == this.seed && nnode.name().equals(this.name());
	}
	
	@Override public boolean IsVolatile(){ return true; }
	@Override public GPNode  GetSimplifiedReplacement(EvolutionState state)
	{ 
		return SimplifyableGPNode.SimplifyableGPNodeImpl.GetSimplifiedReplacement(state, this); 
	} 
	
	// returns converted position cooridinate to align [-1,1] texture window 
	// to various expected noise domains (ie. [0,1],[0,inf]).
	protected float adjustWindow(double pos){ return (float)pos; }
    
    public static class OpenSimplexTwo extends Func_Noise
	{
	    public String toString()         { return "OpenSimplex2"; }
	    public int    expectedChildren() { return 0;         }
	    
	    protected OpenSimplex2 noisegen;
	    
	    @Override
		public void resetNode(EvolutionState state, int thread) 
		{
			seed = state.random[thread].nextLong();
			noisegen = new OpenSimplex2(seed);
		}
	    
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
	        double[] pos = ((TextureProblemForm)problem).Get_Current_Pos();
	        double x = (double)adjustWindow(pos[X.ordinal()]);
	        double y = (double)adjustWindow(pos[Y.ordinal()]);
	        double n = noisegen.noise2(x, y);
	        rd.SetTo(n);
	    }
	}

    public static class Perlin extends Func_Noise
	{
	    public String toString()         { return "Perlin"; }
	    public int    expectedChildren() { return 0;        }
	    
	    protected Noise_Perlin noisegen;
	    
	    @Override
		public void resetNode(EvolutionState state, int thread) 
		{
			seed = state.random[thread].nextLong();
			noisegen = new Noise_Perlin(seed);
		}
	    
	    @Override
	    protected float adjustWindow(double pos)
	    {
	    	pos = ((pos+1.0)/2.0)*4;
	    	return (float)Math.abs(pos);
	    }
	    
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
	        double[] pos = ((TextureProblemForm)problem).Get_Current_Pos();
	        float x = adjustWindow(pos[X.ordinal()]);
	        float y = adjustWindow(pos[Y.ordinal()]);
	        float z = 0f;//adjustWindow(pos[Z.ordinal()]);
	        double n = noisegen.noise(x,y,z); 
	        rd.SetTo(n);
	    }
	}

    public static class Turbulence extends Perlin
	{
		public String toString()         { return "Turbulence"; }
		public int    expectedChildren() { return 1;            }
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
	        double[] pos = ((TextureProblemForm)problem).Get_Current_Pos();
	        
	        children[0].eval(state,thread,input,stack,individual,problem);
	        int octaves = (int)Math.max(1,Math.min(10, rd.GetD() ));
	        
	        float x = adjustWindow(pos[X.ordinal()]);
	        float y = adjustWindow(pos[Y.ordinal()]);
	        float z = 0f;//adjustWindow(pos[Z.ordinal()]);
	        double n = noisegen.turbulentNoise(x,y,z,octaves); 
	        rd.SetTo(n);
	    }
	}

    public static class FractalSum extends Perlin
	{
		public String toString()         { return "FractalSum"; }
		public int    expectedChildren() { return 1;            }
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
	        double[] pos = ((TextureProblemForm)problem).Get_Current_Pos();
	        
	        children[0].eval(state,thread,input,stack,individual,problem);
	        int octaves = (int)Math.max(1,Math.min(10, rd.GetD() ));
	        
	        float x = adjustWindow(pos[X.ordinal()]);
	        float y = adjustWindow(pos[Y.ordinal()]);
	        float z = 0f;//adjustWindow(pos[Z.ordinal()]);
	        double n = noisegen.smoothNoise(x,y,z,octaves); 
	        rd.SetTo(n);
	    }
	}

    public static class Marble extends Perlin
	{
		public String toString()         { return "Marble"; }
		public int    expectedChildren() { return 0;        }
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
	        double[] pos = ((TextureProblemForm)problem).Get_Current_Pos();
	        
	        float x = adjustWindow(pos[X.ordinal()]);
	        float y = adjustWindow(pos[Y.ordinal()]);
	        float z = 0f;//adjustWindow(pos[Z.ordinal()]);
	        double n = noisegen.marble(x,y,z); 
	        rd.SetTo(n);
	    }
	}
}
