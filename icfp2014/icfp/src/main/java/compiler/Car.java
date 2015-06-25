package compiler;

/**
 *
 */
public class Car extends ASTNode {
    private ASTNode list;

    public Car(int lineNumber) {
    }

    public void addChild(ASTNode node) {
   		this.list = node;
   	}

    @Override
    public void cgen(StringBuffer s, boolean addLineComment) {
        list.cgen(s, addLineComment);
        s.append(Instructions.CAR).append(EOL);
    }
}
