package compiler;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

public class If extends ASTNode {
	private static int ifIndex = 0;
	
	List<ASTNode> conditions = new ArrayList<>();
	List<Block> blocks= new ArrayList<>();
	
	@Override
	public void addChild(ASTNode node) {
		if (node instanceof Condition || node instanceof IsInt) {
			conditions.add(node);
		} else if (node instanceof Block) {
			blocks.add((Block) node);
		}
	}

	@Override
	public void cgen(StringBuffer s, boolean addLineComment) {
		Preconditions.checkArgument(conditions.size() == 1 && blocks.size() <= 2);//elsif not supported
		conditions.get(0).cgen(s, addLineComment);
		if (blocks.size() == 1) {
			String ifLabel = getNextLabel();
			String outerLabel = getNextLabel();
			s.append(Instructions.TSEL+" "+ifLabel+" "+outerLabel+"\n");
			s.append(ifLabel+":\n");
			blocks.get(0).cgen(s, false);
			s.append(outerLabel+":\n");
		} else if (blocks.size() ==2 ) {
			String ifLabel = getNextLabel();
			String elseLabel = getNextLabel();
			String outerLabel = getNextLabel();
			s.append(Instructions.TSEL+" "+ifLabel+" "+elseLabel+"\n");
			s.append(ifLabel+":\n");
			blocks.get(0).cgen(s, false);
			s.append(Instructions.LDC+" 1\n");
			s.append(Instructions.TSEL+" "+outerLabel+" null\n");
			s.append(elseLabel+":\n");
			blocks.get(1).cgen(s, false);
			s.append(outerLabel+":\n");
		}
	}

	
	private String getNextLabel() {
		ifIndex++;
		return "if_label"+ifIndex;
	}

}
