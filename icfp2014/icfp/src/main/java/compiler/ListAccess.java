package compiler;

public class ListAccess extends ASTNode {
	private static int laIndex = 0;
	private Variable var;
	private ASTNode index;

	public ListAccess(Variable variable, int lineNumber) {
		super(lineNumber);
        this.var = variable;
	}
	
	@Override
	public void addChild(ASTNode node) {
		this.index = node;
	}
	
	@Override
	public void cgen(StringBuffer s, boolean addLineComment) {
		String conditionLabel = getNextLabel();
		String leftLabel = getNextLabel();
		String atomLabel = getNextLabel();
		String rightLabel = getNextLabel();
		String outerLabel = getNextLabel();
		String outerLabelWithLoad = getNextLabel();
		
		//tmp=0
		s.append(Instructions.LDC).append(" 0");
        if (addLineComment) s.append("\t; ").append(lineNumber);
        s.append("\n");
		s.append(Instructions.ST).append(" 0 0\n");
		
		var.cgen(s, false);
		s.append(conditionLabel+":\n");
		
		//tmp = index ?
		index.cgen(s, false);
		s.append(Instructions.LD).append(" 0 0\n");
		s.append(Instructions.CEQ).append("\n");
		s.append(Instructions.TSEL+" "+leftLabel+" "+rightLabel+"\n");
		
		s.append(leftLabel+":\n");
		//store result in tmp
		s.append(Instructions.ST).append(" 0 0\n");
		s.append(Instructions.LD).append(" 0 0\n");
		s.append(Instructions.ATOM+"\n");
		s.append(Instructions.TSEL+" "+outerLabelWithLoad+" "+atomLabel+"\n");//if integer return directly
		s.append(atomLabel+":\n");
		s.append(Instructions.LD).append(" 0 0\n");
		s.append(Instructions.CAR+"\n");//if not dereference
		//goto outerLabel
		s.append(Instructions.LDC+" 1\n");
		s.append(Instructions.TSEL+" "+outerLabel+" null\n");
		s.append(rightLabel+":\n");
		s.append(Instructions.CDR+"\n");
		//inc tmp
		s.append(Instructions.LD).append(" 0 0\n");
		s.append(Instructions.LDC).append(" 1\n");
		s.append(Instructions.ADD).append("\n");
		s.append(Instructions.ST).append(" 0 0\n");
		//goto conditionLabel
		s.append(Instructions.LDC+" 1\n");
		s.append(Instructions.TSEL+" "+conditionLabel+" null\n");
		s.append(outerLabelWithLoad+":\n");
		s.append(Instructions.LD).append(" 0 0\n");
		s.append(outerLabel+":\n");
	}
	
	private String getNextLabel() {
		laIndex++;
		return "la_label"+laIndex;
	}
}