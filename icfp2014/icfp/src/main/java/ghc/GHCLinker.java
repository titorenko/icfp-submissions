package ghc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import parser.ParserUtil;

public class GHCLinker {
	private Map<String, Integer> labelToLine = new HashMap<>();
	private final String[] asmCode;
	private Map<String, Integer> variables = new HashMap<>();
	private List<String> code = new ArrayList<>();
	private boolean isDebugMode;

	public GHCLinker(List<String> asmCode) {
		this.asmCode = asmCode.toArray(new String[0]);
	}

	public String link() {
		passOne();
		return passTwo();
	}

	private void passOne() {
		//data
		int i = 0;
		int dataIndex = 0;
		for (; i < asmCode.length; i++) {
			asmCode[i] = asmCode[i].trim();
			if (asmCode[i].startsWith(";") || asmCode[i].isEmpty() || asmCode[i].equals(".data")) continue;
			if (asmCode[i].equals(".code")) {
				i++;
				break;
			}
			String[] split = asmCode[i].split(" ");
			variables.put(split[0], dataIndex);
			int inc = split.length == 1 ? 1 : Integer.parseInt(split[1]);
			dataIndex+=inc;
		}
		//code
		int lineNumber = 0;
		for (; i < asmCode.length; i++) {
			asmCode[i] = asmCode[i].trim();
			if (asmCode[i].startsWith("DEBUG ")) {
				if (isDebugMode) {
					asmCode[i] = asmCode[i].substring(6);
				} else {
					continue;
				}
			} 
			if (asmCode[i].endsWith(":")) {
				String label = asmCode[i].substring(0, asmCode[i].length()-1);
				if (labelToLine.containsKey(label)) throw new RuntimeException("Duplicate label: "+label);
				labelToLine.put(label, lineNumber);
			} else if (asmCode[i].startsWith(";") || asmCode[i].isEmpty()){
				//do nothing
			} else {
				code.add(asmCode[i]);
				lineNumber++;
			}
		}
	}
	
	private String passTwo() {
		StringBuffer result = new StringBuffer();
		for (String line : code) {
			if (line.contains("_")) {
				for (Entry<String, Integer> e : labelToLine.entrySet()) {
					line = line.replaceAll(e.getKey(), e.getValue()+"");
				}
			}
			result.append(processDataReferences(line));
			result.append("\n");
		}
		return result.toString();
	}
	
	private String processDataReferences(String command) {
		for (Entry<String, Integer> e : variables.entrySet()) {
			if (command.contains(e.getKey())) {
				command = processDataReference(command, e);
			}
		}
		return command;
	}

	private String processDataReference(String command, Entry<String, Integer> var) {
		String[] split = command.split(" |,|\\[|\\]");
		for (int i = 0; i < split.length; i++) {
			if (split[i].contains(var.getKey())) {
				command = command.replaceAll(Pattern.quote(split[i]), eval(split[i], var)+"");
			}
		}
		return command;
	}

	private String eval(String token, Entry<String, Integer> var) {
		String simpleExpr = token.replaceAll(Pattern.quote(var.getKey()), var.getValue()+"");
		return evalMod256(simpleExpr);
	}

	private String evalMod256(String simpleExpr) {
		if (simpleExpr.contains("+")) {
			String[] split = simpleExpr.split("\\+");
			return addMod256(Integer.parseInt(split[0]), Integer.parseInt(split[1]))+"";
		} 
		if (simpleExpr.contains("-")) {
			String[] split = simpleExpr.split("-");
			return subMod256(Integer.parseInt(split[0]), Integer.parseInt(split[1]))+"";
		}
		return simpleExpr;
	}

	private int addMod256(int x, int y) {
		return (x + y) % 256;
	}

	private int subMod256(int x, int y) {
		int diff = (x - y);
		if (diff < 0) diff = 256 + diff;
		return diff % 256;
	}

	public static void main(String[] args) throws IOException {
        String progName = "/ghost_seek_and_destroy.ghc";
        boolean isDebug = false;
        if (args.length>0) progName = args[0];
        if (args.length>1) isDebug = Boolean.parseBoolean(args[1]);
        InputStream is = ParserUtil.class.getResourceAsStream(progName);
        if (is==null) throw new RuntimeException("File " + progName + " not found in classpath");
        List<String> lines = IOUtils.readLines(is);
        GHCLinker ghcLinker = new GHCLinker(lines);
        ghcLinker.isDebugMode = isDebug;
        String result = ghcLinker.link();
		System.out.println(result );
	}
}
