package compiler;


public abstract class ASTNode {

    protected final static char EOL = '\n';

	protected int lineNumber;

	/**
	 * Builds a new tree node
	 * 
	 * @param lineNumber
	 *            the line in the source file from which this node came.
	 * */
	protected ASTNode(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	protected ASTNode() {
		this.lineNumber = -1;
	}
	
	/** Generate code into print stream corresponding to the node*/
	public abstract void cgen(StringBuffer s, boolean addLineComment);
	
	public void addChild(ASTNode node) {
		throw new UnsupportedOperationException("Not applicable for this node type.");
	}
	
	public void validate() {
		
	}
}
