package compiler;

import java.util.ArrayList;
import java.util.List;

public class FunctionCall extends ASTNode {

	private String funName;
	List<ASTNode> arguments = new ArrayList<>();
	
	public FunctionCall(String funName, int lineNumber) {
		super(lineNumber);
        this.funName = funName;
	}
	
	@Override
	public void addChild(ASTNode node) {
		arguments.add(node);
	}

	@Override
	public void cgen(StringBuffer s, boolean addLineComment) {
        SymbolTable.FunDesc fd = SymbolTable.ST.getFunction(funName, lineNumber);
        s.append(Instructions.LDC).append(" 0");
        if (addLineComment) s.append("\t; ").append(lineNumber); // reserved value
        s.append("\n");
		for (ASTNode astNode : arguments) {
			astNode.cgen(s, false);
		}
        for(int i=0;i<fd.localFrame;i++) {
            s.append(Instructions.LDC).append(" 0\n");
        }
        s.append(Instructions.LDF).append(" ").append(funName).append("\n");
		s.append(Instructions.AP).append(" ").append(arguments.size()+fd.localFrame+1).append("\n");
	}

}
