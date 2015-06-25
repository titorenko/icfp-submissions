package parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import compiler.GCCProgram;

public class ParserUtil {
	
	public static GCCProgram parseResource(String resource) throws IOException {
		InputStream is = ParserUtil.class.getResourceAsStream(resource);
		List<String> lines = IOUtils.readLines(is);
		GCCProgram result = new Parser().parse(lines);
		return result;
	}
}
