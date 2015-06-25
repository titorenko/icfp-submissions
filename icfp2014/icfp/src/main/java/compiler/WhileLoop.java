package compiler;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

public class WhileLoop extends ASTNode {
	private static int whileIndex = 0;
	
	List<ASTNode> conditions = new ArrayList<>();
	List<Block> blocks= new ArrayList<>();
	
	@Override
	public void addChild(ASTNode node) {
		if (node instanceof Condition) {
            conditions.add(node);
        } else if (node instanceof IsInt) {
            conditions.add(node);
		} else if (node instanceof Block) {
			blocks.add((Block) node);
		}
	}

	@Override
	public void cgen(StringBuffer s, boolean addLineComment) {
		Preconditions.checkArgument(conditions.size() == 1 && blocks.size() == 1);
		String loopLabel = getNextLabel();
		String loopBodyLabel = getNextLabel();
		String outerLabel = getNextLabel();
		s.append(loopLabel+":\n");
		conditions.get(0).cgen(s, addLineComment);
		s.append(Instructions.TSEL+" "+loopBodyLabel+" "+outerLabel+"\n");
		s.append(loopBodyLabel+":\n");
		blocks.get(0).cgen(s, false);
		s.append(Instructions.LDC+" 1\n");
		s.append(Instructions.TSEL+" "+loopLabel+" null\n");
		s.append(outerLabel+":\n");
	}

	
	private String getNextLabel() {
		whileIndex++;
		return "while_label"+whileIndex;
	}

}
