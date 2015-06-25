package util;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Properties;

public class TestDataReader {

    private static ObjectMapper mapper = new ObjectMapper();

    private static final String TEST_DATA_DIRECTORY = "src/test/resources/";
    private static final String DUMP_DIRECTORY = "./";

    public static FailureContext readFile(String filename) {
        try {
            return mapper.readValue(new File(filename), FailureContext.class);
        } catch (IOException e) {
            System.out.println("Failed to read context from file.");
            throw new RuntimeException(e);
        }
    }

    public static void writeFile(String filename, FailureContext context) {
        try {
            mapper.writeValue(new File(filename), context);
        } catch (IOException e) {
            System.out.println("Failed to write context to file.");
            e.printStackTrace();
        }
    }

    public static void writeTrainingToProperties(FailureContext context) {
        try {
            Properties properties = context.getProblem()==null
                    ?failureTrainingToProperties(context)
                    :failureRealToProperties(context);
            String filename = TEST_DATA_DIRECTORY+properties.getProperty("id")+".txt";
            FileOutputStream fos = new FileOutputStream(filename);
            properties.store(fos, "");
            fos.close();
        } catch (IOException error) {
            throw new RuntimeException("Failed to write to file", error);
        }
    }

    private static Properties failureRealToProperties(FailureContext context) {
        Properties properties = new Properties();
        properties.setProperty("id", context.getProblem().getId());
        properties.setProperty("ops", Arrays.toString(context.getProblem().getOperators()));
        properties.setProperty("size", ""+context.getProblem().getSize());
        properties.setProperty("x", toString(context.getInputs()));
        properties.setProperty("y", toString(context.getOutputs()));
        properties.setProperty("found", ""+context.getSolution());
        return properties;
    }

    private static Properties failureTrainingToProperties(FailureContext context) {
        Properties properties = new Properties();
        properties.setProperty("id", context.getTrainingProblem().getId());
        properties.setProperty("ops", Arrays.toString(context.getTrainingProblem().getOperators()));
        properties.setProperty("size", ""+context.getTrainingProblem().getSize());
        properties.setProperty("x", toString(context.getInputs()));
        properties.setProperty("y", toString(context.getOutputs()));
        properties.setProperty("solution", ""+context.getTrainingProblem().getChallenge());
        properties.setProperty("found", ""+context.getSolution());
        return properties;
    }

    private static String toString(BigInteger[] data) {
        String[] result = new String[data.length];
        int i=0;
        for(BigInteger integer : data) {
            result[i++] = integer.toString(16);
        }
        return Arrays.toString(result);
    }

    private static void convert1(String name) {
        FailureContext context = readFile(name);
        writeTrainingToProperties(context);
    }

    private static void convertAll(String dir) {
        File directory = new File(dir);
        String[] files = directory.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("dump-");
            }
        });
        for(String file : files) {
            if (new File(TEST_DATA_DIRECTORY + file.substring(5) + ".txt").exists()) {
                System.out.println(file + " : Skipping existing file");
            } else {
                System.out.println(file + " : Converting file");
                convert1(file);
            }
        }
    }

    public static void main(String[] args) {
        //convert1("dump-grS3aISwyRkbKeDVmacUvpAz-failed");
        convertAll(DUMP_DIRECTORY);
    }
}
