package compiler;

/**
 *
 */
public class Cdr extends ASTNode {
    private ASTNode list;

    public Cdr(int lineNumber) {
    }

    public void addChild(ASTNode node) {
   		this.list = node;
   	}

    @Override
    public void cgen(StringBuffer s, boolean addLineComment) {
        list.cgen(s, addLineComment);
        s.append(Instructions.CDR).append(EOL);
    }
}
