package search;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.tuple.Pair;

public class ProblemDefinitionParser {
	public static Pair<Integer, ConstraintStore> getConstraintsFor(String fileName) throws IOException {
		Properties properties = new Properties();
		Properties p = properties;

		p.load(ProblemDefinitionParser.class.getResourceAsStream("/"+fileName));
		final String[] ops = parse(p.getProperty("ops"));
		final int size = Integer.parseInt(p.getProperty("size"));
		final BigInteger[] args = toBigInts(parse(p.getProperty("x")));
		final BigInteger[] values = toBigInts(parse(p.getProperty("y")));
		return new Pair<Integer, ConstraintStore>() {
			private static final long serialVersionUID = -7119591127994118479L;

			@Override
			public ConstraintStore setValue(ConstraintStore value) {
				return new ConstraintStore(ops, args, values);
			}

			@Override
			public Integer getLeft() {
				return size;
			}

			@Override
			public ConstraintStore getRight() {
				return new ConstraintStore(ops, args, values);
			}
		};
	}
	
	private static BigInteger[] toBigInts(String[] strValues) {
		List<BigInteger> result = new ArrayList<BigInteger>();
		for (String s : strValues) {
			result.add(new BigInteger(s.replaceAll("0x", ""), 16));
		}
		return result.toArray(new BigInteger[0]);
	}

	private static String[] parse(String value) {
		String nobrackets = value.replaceAll("[\\[|\\]]", "");
		String[] split = nobrackets.split(",");
		List<String> result = new ArrayList<String>();
		for (String s : split) {
			if (s.trim().isEmpty()) continue;
			result.add(s.trim());
		}
		return result.toArray(new String[0]);
	}
}
