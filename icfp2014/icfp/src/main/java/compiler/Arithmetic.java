package compiler;

import parser.ParsingException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Arithmetic extends ASTNode {

    private final String operation;
    private List<ASTNode> expression = new ArrayList<>(3);

    public Arithmetic(String operation, int lineNumber) {
        super(lineNumber);
        this.operation = operation;
    }


    public void addChild(ASTNode node) {
   		expression.add(node);
   	}

    @Override
    public void cgen(StringBuffer s, boolean addLineComment) {
        expression.get(0).cgen(s, addLineComment);
        expression.get(1).cgen(s, false);
        s.append(getOperation()).append("\n");
    }

    private String getOperation() {
        switch(operation) {
            case "+": return "ADD ";
            case "-": return "SUB ";
            case "*": return "MUL ";
            case "/": return "DIV ";
        }
        throw new ParsingException("Bad operation " + operation, lineNumber);
    }
}
