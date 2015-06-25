package compiler;

import java.util.ArrayList;
import java.util.List;

public class Cons extends ASTNode {

	List<ASTNode> children = new ArrayList<>();
	
	public Cons() {
	}
	
	public void addChild(ASTNode node) {
		children.add(node);
	};

	@Override
	public void cgen(StringBuffer s, boolean addLineComment) {
        for(ASTNode node : children) {
            node.cgen(s, addLineComment);
            addLineComment = false;
        }
        for(int i=0;i<children.size()-1;i++) {
            s.append(Instructions.CONS).append(EOL);
        }
	}
}
