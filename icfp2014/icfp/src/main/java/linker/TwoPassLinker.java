package linker;

import java.util.HashMap;
import java.util.Map;

import compiler.Instructions;

public class TwoPassLinker {

	private String[] asmCode;
	private Map<String, Integer> labelToLine = new HashMap<>();

	public TwoPassLinker(String asmCode) {
		labelToLine.put("null", 0);
		this.asmCode = asmCode.split("\n");
	}

	public String link() {
		passOne();
		return passTwo();
	}

	private void passOne() {
		int lineNumber = 0;
		for (int i = 0; i < asmCode.length; i++) {
			if (asmCode[i].endsWith(":")) {
				String label = asmCode[i].substring(0, asmCode[i].length()-1);
				if (labelToLine.containsKey(label)) throw new RuntimeException("Duplicate label: "+label);
				labelToLine.put(label, lineNumber);
			} else if (!asmCode[i].startsWith(";")){
				lineNumber++;
			}
		}
	}
	
	private String passTwo() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < asmCode.length; i++) {
			if (asmCode[i].endsWith(":")) {
                continue; // get rid of labels
            } else if (asmCode[i].startsWith(";")) {
                continue; // get rid of comments
			} else if (asmCode[i].startsWith(Instructions.LDF.toString())) {
				String funName = asmCode[i].substring(4, asmCode[i].length());
				result.append(Instructions.LDF+" "+labelToLine.get(funName));
				result.append("\n");
			} else if (asmCode[i].startsWith(Instructions.TSEL.toString())) {
				String[] labels = asmCode[i].substring(5, asmCode[i].length()).split(" ");
				result.append(Instructions.TSEL+" "+labelToLine.get(labels[0].trim())+" "+labelToLine.get(labels[1].trim()));
				result.append("\n");
			} else {
				result.append(asmCode[i]);
				result.append("\n");
			}
		}
		return result.toString();
	}
}
