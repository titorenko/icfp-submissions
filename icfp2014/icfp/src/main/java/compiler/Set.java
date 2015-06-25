package compiler;

/**
 * Set value to variable. Oh so non functional.
 */
public class Set extends ASTNode {

    private final String variableName;
    // should leave value on stack
    private ASTNode expression;

    public Set(String variableName, int lineNumber) {
        super(lineNumber);
        this.variableName = variableName;
    }

    public void addChild(ASTNode node) {
        expression = node;
    }

    @Override
    public void cgen(StringBuffer s, boolean addLineComment) {
        expression.cgen(s, addLineComment);
        SymbolTable.VarPtr vp = SymbolTable.ST.getVariable(variableName, lineNumber);
        s.append(Instructions.ST).append(" ").append(vp.frame).append(" ").append(vp.index).append("\n");
    }
}
