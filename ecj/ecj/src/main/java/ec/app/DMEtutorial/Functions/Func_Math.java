package ec.app.DMEtutorial.Functions;

import ec.*;
import ec.app.DMEtutorial.MultiData;
import ec.gp.*;

/**
 * Credit: Michael Gircys
 */
@SuppressWarnings("serial")
public abstract class Func_Math extends GPNode implements SimplifyableGPNode {

	@Override
	public boolean IsVolatile() {
		return false;
	}

	@Override
	public GPNode GetSimplifiedReplacement(EvolutionState state) {
		return SimplifyableGPNode.SimplifyableGPNodeImpl.GetSimplifiedReplacement(state, this);
	}

	public static class Add extends Func_Math {
		public String toString() {
			return "+";
		}

		public int expectedChildren() {
			return 2;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double c1, c2;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			c1 = rd.GetD();
			children[1].eval(state, thread, input, stack, individual, problem);
			c2 = rd.GetD();

			rd.SetTo(c1 + c2);
		}
	}

	public static class Sub extends Func_Math {
		public String toString() {
			return "-";
		}

		public int expectedChildren() {
			return 2;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double c1, c2;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			c1 = rd.GetD();
			children[1].eval(state, thread, input, stack, individual, problem);
			c2 = rd.GetD();

			rd.SetTo(c1 - c2);
		}
	}

	public static class Neg extends Func_Math {
		public String toString() {
			return "-";
		}

		public int expectedChildren() {
			return 1;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double val;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			val = rd.GetD();

			rd.SetTo(0.0 - val);
		}
	}

	public static class Mul extends Func_Math {
		public String toString() {
			return "*";
		}

		public int expectedChildren() {
			return 2;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double c1, c2;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			c1 = rd.GetD();
			children[1].eval(state, thread, input, stack, individual, problem);
			c2 = rd.GetD();

			rd.SetTo(c1 * c2);
		}
	}

	public static class Div extends Func_Math {
		public String toString() {
			return "/";
		}

		public int expectedChildren() {
			return 2;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double num, div;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			num = rd.GetD();
			children[1].eval(state, thread, input, stack, individual, problem);
			div = rd.GetD();

			rd.SetTo((div != 0) ? (num / div) : 0);
		}
	}

	public static class Sin extends Func_Math {
		public String toString() {
			return "sin";
		}

		public int expectedChildren() {
			return 1;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double v;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			v = rd.GetD();

			rd.SetTo(Math.sin(v));
		}
	}

	public static class Cos extends Func_Math {
		public String toString() {
			return "cos";
		}

		public int expectedChildren() {
			return 1;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double v;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			v = rd.GetD();

			rd.SetTo(Math.cos(v));
		}
	}

	public static class Tan extends Func_Math {
		public String toString() {
			return "tan";
		}

		public int expectedChildren() {
			return 1;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double v;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			v = rd.GetD();

			v = Math.tan(v);
			if (Double.isInfinite(v) || Double.isNaN(v))
				v = 0.0;

			rd.SetTo(v);
		}
	}

	public static class Abs extends Func_Math {
		public String toString() {
			return "abs";
		}

		public int expectedChildren() {
			return 1;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double v;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			v = rd.GetD();

			rd.SetTo(Math.abs(v));
		}
	}

	public static class Pow extends Func_Math {
		public String toString() {
			return "pow";
		}

		public int expectedChildren() {
			return 2;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double base, exp;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			base = rd.GetD();
			children[1].eval(state, thread, input, stack, individual, problem);
			exp = rd.GetD();

			rd.SetTo(Math.pow(base, exp));
		}
	}

	public static class Exp extends Func_Math {
		public String toString() {
			return "exp";
		}

		public int expectedChildren() {
			return 1;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double exp;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			exp = rd.GetD();

			rd.SetTo(Math.exp(exp));
		}
	}

	public static class Sqrt extends Func_Math {
		public String toString() {
			return "sqrt";
		}

		public int expectedChildren() {
			return 1;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double v;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			v = rd.GetD();

			rd.SetTo(Math.sqrt(Math.abs(v)));
		}
	}

	public static class Square extends Func_Math {
		public String toString() {
			return "pow2";
		}

		public int expectedChildren() {
			return 1;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double v;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			v = rd.GetD();

			rd.SetTo(Math.pow(v, 2.0));
		}
	}

	public static class Cube extends Func_Math {
		public String toString() {
			return "pow3";
		}

		public int expectedChildren() {
			return 1;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double v;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			v = rd.GetD();

			rd.SetTo(Math.pow(v, 3.0));
		}
	}

	public static class Max extends Func_Math {
		public String toString() {
			return "max";
		}

		public int expectedChildren() {
			return 2;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double c1, c2;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			c1 = rd.GetD();
			children[1].eval(state, thread, input, stack, individual, problem);
			c2 = rd.GetD();

			rd.SetTo(Math.max(c1, c2));
		}
	}

	public static class Min extends Func_Math {
		public String toString() {
			return "min";
		}

		public int expectedChildren() {
			return 2;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double c1, c2;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			c1 = rd.GetD();
			children[1].eval(state, thread, input, stack, individual, problem);
			c2 = rd.GetD();

			rd.SetTo(Math.min(c1, c2));
		}
	}

	public static class Avg extends Func_Math {
		public String toString() {
			return "avg";
		}

		public int expectedChildren() {
			return 2;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double c1, c2;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			c1 = rd.GetD();
			children[1].eval(state, thread, input, stack, individual, problem);
			c2 = rd.GetD();

			rd.SetTo((c1 + c2) / 2.0);
		}
	}

	public static class Log10 extends Func_Math {
		public String toString() {
			return "log_10";
		}

		public int expectedChildren() {
			return 1;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double v;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			v = rd.GetD();

			rd.SetTo(Math.log10(v));
		}
	}

	public static class LogE extends Func_Math {
		public String toString() {
			return "log_e";
		}

		public int expectedChildren() {
			return 1;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double v;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			v = rd.GetD();

			rd.SetTo(Math.log(v));
		}
	}

	public static class Round extends Func_Math {
		public String toString() {
			return "round";
		}

		public int expectedChildren() {
			return 1;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double v;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			v = rd.GetD();

			rd.SetTo(Math.round(v));
		}
	}

	public static class Floor extends Func_Math {
		public String toString() {
			return "floor";
		}

		public int expectedChildren() {
			return 1;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double v;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			v = rd.GetD();

			rd.SetTo(Math.floor(v));
		}
	}

	public static class Ceil extends Func_Math {
		public String toString() {
			return "ceil";
		}

		public int expectedChildren() {
			return 1;
		}

		public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
				final GPIndividual individual, final Problem problem) {
			double v;
			MultiData rd = ((MultiData) (input));

			children[0].eval(state, thread, input, stack, individual, problem);
			v = rd.GetD();

			rd.SetTo(Math.ceil(v));
		}
	}

	public static class Lerp extends Func_Math
	{
	    public String toString()         { return "lerp"; }
	    public int    expectedChildren() { return 3;   }
	    public void eval(
	    	final EvolutionState state,
	        final int            thread,
	        final GPData         input,
	        final ADFStack       stack,
	        final GPIndividual   individual,
	        final Problem        problem
	    )
	    {
	        double v1, v2, w, v;
	        MultiData rd = ((MultiData)(input));

	        children[0].eval(state,thread,input,stack,individual,problem);
	        v1 = rd.GetD();
	        children[1].eval(state,thread,input,stack,individual,problem);
	        v2 = rd.GetD();
	        children[2].eval(state,thread,input,stack,individual,problem);
	        w = rd.GetD();

	        w = Math.max(Math.min(1.0, w), 0.0);
	        v = (v2 - v1);
	        v = (v*w) + v1;

	        rd.SetTo(  v  );
	    }
	}
}
