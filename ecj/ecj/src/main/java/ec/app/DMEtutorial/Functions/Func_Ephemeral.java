package ec.app.DMEtutorial.Functions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ec.EvolutionState;
import ec.Problem;
import ec.app.DMEtutorial.MultiData;
import ec.gp.ADFStack;
import ec.gp.ERC;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.gp.SimplifyableGPNode;
import ec.util.Code;
import ec.util.DecodeReturn;

/**
 * Credit: Michael Gircys
 */
@SuppressWarnings("serial")
public abstract class Func_Ephemeral extends ERC implements SimplifyableGPNode {
	public double value;

	@Override
	public int expectedChildren() {
		return 0;
	}

	@Override
	public String name() {
		return "E";
	}

	@Override
	public String toStringForHumans() {
		return name() + "[" + String.valueOf(value) + "]";
	}

	@Override
	public String encode() {
		return Code.encode(value);
	}

	@Override
	public boolean decode(DecodeReturn dret) {
		// From the manual.
		int pos = dret.pos;
		String data = dret.data;
		Code.decode(dret);
		if (dret.type != DecodeReturn.T_DOUBLE) {
			dret.data = data;
			dret.pos = pos;
			return false;
		}
		value = dret.d;
		return true;
	}

	@Override
	public void readNode(EvolutionState state, DataInput input) throws IOException {
		value = input.readDouble();
	}

	@Override
	public void writeNode(EvolutionState state, DataOutput output) throws IOException {
		output.writeDouble(value);
	}

	@Override
	public boolean nodeEquals(GPNode node) {
		if (!(node instanceof Func_Ephemeral))
			return false;
		return Math.abs(((Func_Ephemeral) node).value - this.value) < 1.0E-15;
	}

	@Override
	public boolean IsVolatile() {
		return false;
	}

	@Override
	public GPNode GetSimplifiedReplacement(EvolutionState state) {
		return this;
	}

	public void eval(final EvolutionState state, final int thread, final GPData input, final ADFStack stack,
			final GPIndividual individual, final Problem problem) {

		// if (input == null)
		// state.output.message("null input");
		MultiData rd = ((MultiData) (input));
		// if (rd == null)
		// state.output.message("null");
		rd.SetTo(value);
	}

	// Eph_1 --> [0.0, 1.0]
	public static class Eph_1 extends Func_Ephemeral {
		@Override
		public void resetNode(EvolutionState state, int thread) {
			value = state.random[thread].nextDouble(true, true);
		}

		@Override
		public void mutateERC(EvolutionState state, int thread) {
			double v;
			do
				v = value + state.random[thread].nextGaussian() * 0.01;
			while (v < 0.0 || v > 1.0);
			value = v;
		}
	}

	// Eph_10 --> [0.0, 10.0]
	public static class Eph_10 extends Func_Ephemeral {
		@Override
		public void resetNode(EvolutionState state, int thread) {
			value = state.random[thread].nextDouble(true, true) * 10.0;
		}

		@Override
		public void mutateERC(EvolutionState state, int thread) {
			double v;
			do
				v = value + state.random[thread].nextGaussian() * 0.1;
			while (v < 0.0 || v > 10.0);
			value = v;
		}
	}

	// Eph_100 --> [0.0, 100.0]
	public static class Eph_100 extends Func_Ephemeral {
		@Override
		public void resetNode(EvolutionState state, int thread) {
			value = state.random[thread].nextDouble(true, true) * 100.0;
		}

		@Override
		public void mutateERC(EvolutionState state, int thread) {
			double v;
			do
				v = value + state.random[thread].nextGaussian() * 1.0;
			while (v < 0.0 || v > 100.0);
			value = v;
		}
	}

}
