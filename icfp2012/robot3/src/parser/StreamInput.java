package parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 *
 */
public class StreamInput implements Input {

    private final BufferedReader reader;

    public StreamInput(String filename) {
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to open map file", e);
        }
    }

    public StreamInput(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    public StreamInput(InputStream inputStream) {
        reader = new BufferedReader(new InputStreamReader(inputStream));
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
