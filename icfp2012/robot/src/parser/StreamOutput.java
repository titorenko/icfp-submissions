package parser;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

public class StreamOutput implements Output {
    private final PrintWriter stream;

    public StreamOutput(OutputStream stream) {
        this.stream = new PrintWriter(stream);
    }

    public StreamOutput(Writer writer) {
        this.stream = new PrintWriter(writer);
    }

    @Override
    public void printLine(String line) {
        stream.println(line);
    }
}
