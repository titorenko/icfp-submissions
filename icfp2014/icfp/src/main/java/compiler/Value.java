package compiler;

public class Value extends ASTNode {

    private final int value;

    public Value(int value, int lineNumber) {
        super(lineNumber);
        this.value = value;
    }

    @Override
    public void cgen(StringBuffer s, boolean addLineComment) {
        s.append(Instructions.LDC).append(" ").append(value);
        if (addLineComment) s.append("\t; ").append(lineNumber);
        s.append("\n");
    }
}
