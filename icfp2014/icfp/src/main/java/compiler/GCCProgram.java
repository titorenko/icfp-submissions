package compiler;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import static compiler.Instructions.*;

public class GCCProgram extends ASTNode {
	
	private InitialState initialState = new InitialState();
	private Function stepFunction;
    private Function initFunction;
	private Block mainBlock;
	private List<Function> helperFunctions = new ArrayList<Function>();
	private boolean debugMode;

    public GCCProgram() {
		super(0);
	}
	
	public void addChild(ASTNode node) {
		if (node instanceof Block) {
			mainBlock = (Block) node;
		} else {
			Function fun = (Function) node;
			if (fun.getName().equals("step")) {
                this.stepFunction = fun;
            } else if (fun.getName().equals("init")) {
                this.initFunction = fun;
			} else {
				helperFunctions.add(fun);
			}
		}
	}

	@Override
	public void cgen(StringBuffer s, boolean addLineComment) {
		if (debugMode) {
			cgenDebug(s, addLineComment);
			return;
		}
        if (initFunction!=null) {
            s.append(LD).append(" 0 0").append(EOL);
            s.append(LD).append(" 0 1").append(EOL);
            s.append(LDF).append(" ").append("init_internal").append(EOL);
            s.append(AP).append(" 2").append(EOL);
        } else {
            s.append(LDC).append(" 0").append(EOL);
        }
		s.append(LDF).append(" ").append("step_internal").append(EOL);
		s.append(CONS).append(EOL);
		s.append(RTN).append(EOL);
        // built-ins
        builtInInit(s);
        builtInStep(s);
        // functions
        if (initFunction!=null) initFunction.cgen(s, addLineComment);
		stepFunction.cgen(s, addLineComment);
		for (Function f : helperFunctions) {
			f.cgen(s, addLineComment);
		}
	}

    // function to invoke hi step code with proper stack frame
    private void builtInStep(StringBuffer s) {
        Function f = new Function("step_internal", "aiState", "worldState");
        f.setTopLevel(true);
        Block b = new Block();
        FunctionCall fc = new FunctionCall(stepFunction.getName(), 0);
        fc.addChild(new Variable("aiState", 0));
        fc.addChild(new Variable("worldState", 0));
        b.addChild(fc);
        f.addChild(b);
        f.cgen(s, false);
    }

    // function to bootstrap init code from client. calls init
    private void builtInInit(StringBuffer s) {
        if (initFunction==null) return;
        SymbolTable.FunDesc fd = SymbolTable.ST.getFunction(initFunction.getName(), lineNumber);
        s.append("; function ").append("init_internal").append(EOL);
		s.append("init_internal").append(":\n");
        s.append(LDC).append(" 0").append(EOL);
        s.append(LD).append(" 0 0").append(EOL);
        s.append(LD).append(" 0 1").append(EOL);
        for(int i=0;i<fd.localFrame;i++) {
            s.append(Instructions.LDC).append(" 0\n");
        }
        s.append(LDF).append(" ").append(initFunction.getName()).append(EOL);
		s.append(AP).append(" ").append(2+fd.localFrame+1).append(EOL);
        s.append(RTN).append(EOL);
    }

	private void cgenDebug(StringBuffer s, boolean addLineComment) {
		mainBlock.cgen(s, addLineComment);
		if (stepFunction != null) stepFunction.cgen(s, addLineComment);
		for (Function f : helperFunctions) {
			f.cgen(s, false);
		}		
	}

	public void validate() {
		Preconditions.checkArgument(stepFunction != null, "Function 'step' is not defined.");
		stepFunction.validate();
		for (Function function : helperFunctions) {
			function.validate();
		}
	}

	public void setDebugMode() {
		this.debugMode = true;
	}
}