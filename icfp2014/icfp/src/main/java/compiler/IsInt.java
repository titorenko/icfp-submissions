package compiler;

/**
 *
 */
public class IsInt extends ASTNode {
    private ASTNode value;

    public IsInt(int lineNumber) {
    }

    public void addChild(ASTNode node) {
   		this.value = node;
   	}

    @Override
    public void cgen(StringBuffer s, boolean addLineComment) {
        value.cgen(s, addLineComment);
        s.append(Instructions.ATOM).append(EOL);
    }
}
