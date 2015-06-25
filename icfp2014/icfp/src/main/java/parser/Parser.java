package parser;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import compiler.*;

public class Parser {
	
	Stack<ASTNode> nodes = new Stack<>();

    int lineNumber;

	public GCCProgram parse(List<String> program) {
		nodes.push(new GCCProgram());
		lineNumber = 1;
        try {
            for (String line : program) {
                parseLine(line);
                lineNumber++;
            }
        } catch (ParsingException pex) {
            throw pex;
        } catch (RuntimeException rex) {
            throw new ParsingException("Failed to parse line", lineNumber, rex);
        }
		return (GCCProgram) nodes.pop();
	}

	private void parseLine(String line) {
		line = line.trim();
		if (line.isEmpty() || line.startsWith("//")) {
			return;
		} else if (line.startsWith("fun ")) {
			parseFunction(line);
            Function fnode = (Function)nodes.peek();
            SymbolTable.ST.addFunction(new SymbolTable.FunDesc(fnode.getName(), 0));
		} else if (line.startsWith("endif")) {
			Preconditions.checkState(nodes.pop() instanceof Block, "Line: "+lineNumber);
			Preconditions.checkState(nodes.pop() instanceof If, "Line: "+lineNumber);
		} else if (line.startsWith("endloop")) {
			Preconditions.checkState(nodes.pop() instanceof Block, "Line: "+lineNumber);
			Preconditions.checkState(nodes.pop() instanceof WhileLoop, "Line: "+lineNumber);
		} else if (line.startsWith("end")) {
			Preconditions.checkState(nodes.pop() instanceof Block, "Line: " + lineNumber);
            if (!(nodes.peek() instanceof GCCProgram)) nodes.pop();
        } else if (line.startsWith("var")) {
            String[] variables = parseVariablesDefinition(line);
            Function fnode = (Function)nodes.peek();
            fnode.addVariables(variables);
            SymbolTable.ST.addFunction(new SymbolTable.FunDesc(fnode.getName(), fnode.getVariables().length));
        } else if (line.startsWith("set ")) {
            parseSetVariable(line);
//        } else if (line.startsWith("global")) {
//            String[] variables = parseVariablesDefinition(line);
//            ((GCCProgram)nodes.peek()).setGlobalVariables(variables);
		} else if (line.startsWith("begin")) {
			Block block = new Block();
			nodes.peek().addChild(block);
			nodes.push(block);
		} else if (line.startsWith("cons")) {
			parseCons(line);
		} else if (line.startsWith("call")) {
			parseFunctionCall(line);
		} else if (line.startsWith("if")) {
			parseIf(line);
		} else if (line.startsWith("else")) {
			Preconditions.checkState(nodes.pop() instanceof Block);
			Block block = new Block();
			nodes.peek().addChild(block);
			nodes.push(block);
		} else if (line.startsWith("while")) {
			parseWhileLoop(line);
		} else if (line.startsWith("return")) {
			parseReturn(line);
		} else if (line.startsWith("debug")) {
            parseDebug(line);
        } else if (line.startsWith("define")) {
            parseDefine(line);
        } else {
            throw new ParsingException("Can not parse " + line, lineNumber);
        }
	}

    private static Pattern define = Pattern.compile("define\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*=\\s*((-?[0-9]+)|([a-zA-Z][a-zA-Z0-9]*))\\s*");
    private void parseDefine(String line) {
        Matcher m = define.matcher(line);
        if (m.matches()) {
            SymbolTable.ST.addDefine(m.group(1), m.group(2));
        } else {
            throw new ParsingException("Can't parse define " + line, lineNumber);
        }
    }

    private void parseReturn(String line) {
		Return returnSt = new Return();
		nodes.peek().addChild(returnSt);
		nodes.push(returnSt);
		parseExpression(line.substring(7));
		nodes.pop();
	}

    private void parseDebug(String line) {
        String[] split = line.trim().split(" +");
        ASTNode debug = new Debug();
        nodes.push(debug);
        parseExpression(split[1]);
        nodes.pop();
        nodes.peek().addChild(debug);
    }

	private void parseWhileLoop(String line) {
		WhileLoop loop = new WhileLoop();
		nodes.peek().addChild(loop);
		nodes.push(loop);
		parseCondition(line.substring(6));
		Block block = new Block();
		loop.addChild(block);
		nodes.push(block);
	}

    private void parseSetVariable(String line) {
        String[] parts = line.substring(4).trim().split("\\s*=\\s*");
        ASTNode setNode = new Set(parts[0], lineNumber);
        nodes.push(setNode);
        parseExpression(parts[1]);
        nodes.pop();
        nodes.peek().addChild(setNode);
    }

    private void parseIf(String line) {
		If ifStatement = new If();
		nodes.peek().addChild(ifStatement);
		nodes.push(ifStatement);
		parseCondition(line.substring(3));
		Block block = new Block();
		ifStatement.addChild(block);
		nodes.push(block);
	}

    private Pattern condition = Pattern.compile("(.+?)\\s*((==)|(>=)|(>))\\s*(.+?)\\s*");
    private Pattern isint = Pattern.compile("isint\\s+(.+?)\\s*");
	private void parseCondition(String line) {
        if (line.startsWith("isint ")) {
            Matcher im = isint.matcher(line);
            if (im.matches()) {
                ASTNode node = new IsInt(lineNumber);
                nodes.push(node);
                parseExpression(im.group(1));
                nodes.pop();
                nodes.peek().addChild(node);
            } else {
                throw new ParsingException("Failed to parse condition " + line, lineNumber);
            }
        } else {
            Matcher cm = condition.matcher(line);
            if (cm.matches()) {
                Condition condition = new Condition(cm.group(2), lineNumber);
                nodes.push(condition);
                parseExpression(cm.group(1));
                parseExpression(cm.group(6));
                nodes.pop();
                nodes.peek().addChild(condition);
            } else {
                throw new ParsingException("Failed to parse condition " + line, lineNumber);
            }
        }
	}

	private void parseFunctionCall(String line) {
		String[] split = line.split("\\s+");
		FunctionCall functionCall = new FunctionCall(split[1], lineNumber);
		nodes.push(functionCall);
		for (int i = 2; i < split.length; i++) {
            parseToken(split[i]);
		}
		nodes.pop();
		nodes.peek().addChild(functionCall);
	}

    private static final String TOKEN_EXPR = "((-?[0-9]+)|(:?[_a-zA-Z][_a-zA-Z0-9]*))";
    private static final String IDENTIFIER = "([_a-zA-Z][_a-zA-Z0-9]*)";
    private static Pattern constant = Pattern.compile(TOKEN_EXPR);
    private static Pattern arithmetic = Pattern.compile(TOKEN_EXPR+"\\s*([\\+\\-\\*/])\\s*"+TOKEN_EXPR);
    private static Pattern functionCall = Pattern.compile("call\\s+(.+)");
    private static Pattern listAccess = Pattern.compile(IDENTIFIER+"\\s*\\["+TOKEN_EXPR+"\\]");
    private static Pattern cons = Pattern.compile("cons.*");
    private static Pattern car = Pattern.compile("car\\s+(.+)");
    private static Pattern cdr = Pattern.compile("cdr\\s+(.+)");
    private void parseExpression(String expr) {
        Matcher am = arithmetic.matcher(expr);
        if (am.matches()) {
            ASTNode ar = new Arithmetic(am.group(4), lineNumber);
            nodes.push(ar);
            parseToken(am.group(1));
            parseToken(am.group(5));
            nodes.pop();
            nodes.peek().addChild(ar);
            return;
        }
        am = constant.matcher(expr);
        if (am.matches()) {
            parseToken(am.group(1));
            return;
        }
        am = functionCall.matcher(expr);
        if (am.matches()) {
            parseFunctionCall(expr);
            return;
        }
        am = cons.matcher(expr);
        if (am.matches()) {
            parseCons(expr);
            return;
        }
        am = listAccess.matcher(expr);
        if (am.matches()) {
        	parseListAccess(am.group(1), am.group(2));
        	return;
        }
        am = car.matcher(expr);
        if (am.matches()) {
            parseCar(am.group(1));
            return;
        }
        am = cdr.matcher(expr);
        if (am.matches()) {
            parseCdr(am.group(1));
            return;
        }
        throw new ParsingException(expr, lineNumber);
    }

    private void parseCar(String variable) {
        ASTNode node = new Car(lineNumber);
        nodes.push(node);
        parseToken(variable);
        nodes.pop();
        nodes.peek().addChild(node);
    }

    private void parseCdr(String variable) {
        ASTNode node = new Cdr(lineNumber);
        nodes.push(node);
        parseToken(variable);
        nodes.pop();
        nodes.peek().addChild(node);
    }

    private void parseListAccess(String value, String index) {
    	ListAccess listAccess = new ListAccess(new Variable(value, lineNumber), lineNumber);
    	nodes.push(listAccess);
    	parseExpression(index);
    	nodes.pop();
		nodes.peek().addChild(listAccess);
	}

	private void parseToken(String value) {
        if (value.startsWith(":")) {
            value = SymbolTable.ST.getDefine(value.substring(1), lineNumber);
        }
        if (Character.isLetter(value.charAt(0))) {
            nodes.peek().addChild(new Variable(value, lineNumber));
        } else {
            nodes.peek().addChild(new Value(Integer.parseInt(value), lineNumber));
        }
    }

    private String[] parseVariablesDefinition(String line) {
        return line.substring(line.indexOf(" ")).trim().split(" *, *");
    }

    private void parseCons(String line) {
		Cons cons = new Cons();
		nodes.push(cons);
		String[] split = line.split("\\s+");
		for (int i = 1; i < split.length; i++) {
            parseToken(split[i]);
		}
		nodes.pop();
		nodes.peek().addChild(cons);
	}

	private void parseFunction(String line) {
		String[] split = line.split("\\s+");
		Function fun = new Function(split[1], Arrays.copyOfRange(split, 2, split.length));
		nodes.peek().addChild(fun);
		nodes.push(fun);
	}
}
