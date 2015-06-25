package compiler;

import static compiler.Instructions.LDC;

public class InitialState {

	public void cgen(StringBuffer s) {
		s.append(LDC+" 0\n");
	}

}
