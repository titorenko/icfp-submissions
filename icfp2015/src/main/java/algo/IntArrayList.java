package algo;

import java.util.Arrays;
import java.util.stream.IntStream;

import com.carrotsearch.hppc.procedures.IntProcedure;

public class IntArrayList extends com.carrotsearch.hppc.IntArrayList {
	
	public IntArrayList(int[] values) {
		super(values.length);
		add(values);
	}

	public IntArrayList() {
		super();
	}

	
	public IntStream stream() {
		return Arrays.stream(buffer, 0, size());
	}

	public void forAll(IntProcedure procedure) {
		forEach(procedure);
	}
}