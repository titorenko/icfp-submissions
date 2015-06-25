package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class StringInput implements Input {

    private final BufferedReader reader;

    public StringInput(String string) {
        this.reader = new BufferedReader(new StringReader(string));
    }

    @Override
    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read input.", e);
        }
    }
}
