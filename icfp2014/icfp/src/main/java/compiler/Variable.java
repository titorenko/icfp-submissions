package compiler;

public class Variable extends ASTNode {
    private final String name;

    public Variable(String name, int lineNumber) {
        super(lineNumber);
        this.name = name;
    }

    @Override
    public void cgen(StringBuffer s, boolean addLineComment) {
        SymbolTable.VarPtr vp = SymbolTable.ST.getVariable(name, lineNumber);
        s.append(Instructions.LD).append(" ").append(vp.frame).append(" ").append(vp.index);
        if (addLineComment) s.append("\t; ").append(lineNumber);
        s.append("\n");
    }
}
