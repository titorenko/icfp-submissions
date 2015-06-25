package lambdaman;

import java.io.IOException;

import linker.TwoPassLinker;
import parser.ParserUtil;

import compiler.GCCProgram;

public class DebugCompiler {
	public static void main(String[] args) throws IOException {
        String progName = "/simple.gcc";
        if (args.length>0) progName = args[0];
		GCCProgram gcc = ParserUtil.parseResource(progName);
		gcc.setDebugMode();
		StringBuffer buffer = new StringBuffer();
		gcc.cgen(buffer, true);
		/*System.out.println(buffer.toString());
		System.out.println("---");*/
		String result = new TwoPassLinker(buffer.toString()).link();
		System.out.println(result );
	}
}
