package compiler;

public class Return extends ASTNode {

	private ASTNode node;

	@Override
	public void addChild(ASTNode node) {
		this.node = node;
	}
	
	@Override
	public void cgen(StringBuffer s, boolean addLineComment) {
		node.cgen(s, addLineComment);
		s.append(Instructions.RTN).append("\n");
	}
}
