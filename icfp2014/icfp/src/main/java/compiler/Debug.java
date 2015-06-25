package compiler;

/**
 *
 */
public class Debug extends ASTNode {

    private ASTNode child;

    public void addChild(ASTNode node) {
   		child = node;
   	}

    @Override
    public void cgen(StringBuffer s, boolean addLineComment) {
        child.cgen(s, addLineComment);
        s.append(Instructions.DBUG).append("\n");
    }
}
