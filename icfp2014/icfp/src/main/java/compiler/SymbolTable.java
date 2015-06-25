package compiler;


import parser.ParsingException;

import java.util.*;

public class SymbolTable {
	public static final SymbolTable ST = new SymbolTable();
	
    private Map<String, FunDesc> functionTable = new HashMap<>(); // number of

    private Map<String,String> defines = new HashMap<>();

    private List<Map<String, Integer>> namespaces = new ArrayList<>();

    public void addFunction(FunDesc func) {
        functionTable.put(func.name, func);
    }

    public FunDesc getFunction(String name, int lineNumber) {
        if (functionTable.containsKey(name)) return functionTable.get(name);
        throw new IllegalArgumentException("Function " + name + " is not defined @ line " + lineNumber);
    }

    // create frame and fill with variables
    public void pushFrame(String ... variables) {
        Map<String,Integer> frame = new HashMap<>();
        for(int i=0;i<variables.length;i++) {
            frame.put(variables[i], i);
        }
        namespaces.add(frame);
    }

    // get rid of last frame
    public void popFrame() {
        namespaces.remove(namespaces.size()-1);
    }

    // get variable from scope
    public VarPtr getVariable(String name, int lineNumber) {
        for(int i=0;i<namespaces.size();i++) {
            Integer index = namespaces.get(namespaces.size()-i-1).get(name);
            if (index!=null) return new VarPtr(i, index);
        }
        throw new ParsingException("Unknown variable " + name, lineNumber);
    }

    public void addDefine(String key, String value) {
        defines.put(key, value);
    }

    public String getDefine(String key, int lineNumber) {
        if (!defines.containsKey(key)) throw new ParsingException("Unknown macro " + key, lineNumber);
        return defines.get(key);
    }

    public static class VarPtr {
        public int frame;
        public int index;

        public VarPtr(int frame, int index) {
            this.frame = frame;
            this.index = index;
        }
    }

    public static class FunDesc {
        public final String name;
        public final int localFrame;

        public FunDesc(String name, int localFrame) {
            this.name = name;
            this.localFrame = localFrame;
        }
    }
}
