package compiler;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import parser.ParsingException;

public class Condition extends ASTNode {

	private final String op;
	List<ASTNode> operands = new ArrayList<>();

	public Condition(String op, int lineNumber) {
        super(lineNumber);
		Preconditions.checkArgument("==".equals(op) || ">".equals(op) || ">=".equals(op));
		this.op = op;
	}
	
	@Override
	public void addChild(ASTNode node) {
		operands.add(node);
	}

	@Override
	public void cgen(StringBuffer s, boolean addLineComment) {
		operands.get(0).cgen(s, addLineComment);
		operands.get(1).cgen(s, false);
		s.append(getInstruction());
		s.append("\n");
	}

	private Instructions getInstruction() {
		switch(op) {
		case "==" : return Instructions.CEQ;
		case ">" : return Instructions.CGT;
		case ">=" : return Instructions.CGTE;
		}
		throw new ParsingException("Unknown operation " + op, lineNumber);
	}
}
