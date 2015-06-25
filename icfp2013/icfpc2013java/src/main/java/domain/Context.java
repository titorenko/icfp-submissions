package domain;

import java.math.BigInteger;

public class Context {
	private Identifier inputId;
	private Identifier fold0Id;
	private Identifier fold1Id;
	
	private BigInteger input;
	private BigInteger fold0;
	private BigInteger fold1;

    public static Context contextForPartialEvaluation(BigInteger input) {
        Context ctx = new Context(new Identifier("x"), input);
//        ctx.setFold0Id(new Identifier("y"));
//        ctx.setFold1Id(new Identifier("a"));
        return ctx;
    }

    public Context(Identifier inputId, BigInteger input) {
        this.input = input;
        this.inputId = inputId;
    }

    public void setFold0Value(BigInteger value) {
        fold0 = value;
    }

    public void setFold1Value(BigInteger value) {
        fold1 = value;
    }

    public void setInputValue(BigInteger value) {
        input = value;
    }

    public void setInputId(Identifier inputId) {
        this.inputId = inputId;
    }

    public void setFold0Id(Identifier fold0Id) {
        this.fold0Id = fold0Id;
    }

    public void setFold1Id(Identifier fold1Id) {
        this.fold1Id = fold1Id;
    }

    public Identifier getInputId() {
        return inputId;
    }

    public Identifier getFold0Id() {
        return fold0Id;
    }

    public Identifier getFold1Id() {
        return fold1Id;
    }

    public BigInteger getValueOf(Identifier identifier) {
		if (identifier.equals(inputId)) {
			return input;
		} else if (identifier.equals(fold0Id)) {
			return fold0;
		} else if (identifier.equals(fold1Id)) {
			return fold1;
		}
		throw new IllegalArgumentException("Unknown identifier: "+identifier);
	}
}
