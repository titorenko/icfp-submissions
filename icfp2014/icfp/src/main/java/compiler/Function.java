package compiler;

import static compiler.Instructions.*;

public class Function extends ASTNode {

    public static final String RESERVED_VARIABLE = "RESERVED";

	private final String name;
	private Block body;
	private String[] params = new String[0];
    private String[] variables = new String[0];
    private boolean topLevel = false;

	public Function(String name, String ... params) {
		this.name = name;
		this.params = params;
	}
	
	@Override
	public void addChild(ASTNode node) {
		this.body = (Block) node;
	}
	
    public void addVariables(String[] variables) {
        String[] allVariables = new String[variables.length+this.variables.length];
        System.arraycopy(this.variables, 0, allVariables, 0, this.variables.length);
        System.arraycopy(variables, 0, allVariables, this.variables.length, variables.length);
        this.variables = allVariables;
    }

    public void setTopLevel(boolean topLevel) {
        this.topLevel = topLevel;
    }

	@Override
	public void cgen(StringBuffer s, boolean addLineComment) {
        s.append("; function ").append(name).append("\n");
		s.append(name);
		s.append(":\n");
        String[] allVars;
        if (topLevel) {
            allVars = params;
        } else {
            allVars = new String[params.length + variables.length + 1];
            allVars[0] = RESERVED_VARIABLE;
            System.arraycopy(params, 0, allVars, 1, params.length);
            System.arraycopy(variables, 0, allVars, params.length + 1, variables.length);
        }
        SymbolTable.ST.pushFrame(allVars);
		body.cgen(s, addLineComment);
        SymbolTable.ST.popFrame();
		s.append(RTN);
		s.append("\n");
	}

	public String getName() {
		return name;
	}

    public String[] getVariables() {
        return variables;
    }
}