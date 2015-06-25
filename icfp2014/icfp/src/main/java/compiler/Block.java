package compiler;

import java.util.ArrayList;
import java.util.List;


public class Block extends ASTNode {
	List<ASTNode> children = new ArrayList<>();
	
	@Override
	public void addChild(ASTNode node) {
		children.add(node);
	}
	
	@Override
	public void cgen(StringBuffer s, boolean addLineComment) {
		for (ASTNode astNode : children) {
			astNode.cgen(s, addLineComment);
		}
	}
}
