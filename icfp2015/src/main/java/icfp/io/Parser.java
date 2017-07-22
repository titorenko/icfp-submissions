package icfp.io;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import icfp.io.model.Problem;
import icfp.io.model.Submission;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Convert file or resource to Problem.
 */
public class Parser {

    private ObjectMapper mapper = new ObjectMapper();

    public Problem parse(String name) {
        try {
            File src = new File(name);
            if (src.exists())
                return mapper.readValue(src, Problem.class);
            InputStream resource = Parser.class.getResourceAsStream(name);
            if (resource!=null)
                return mapper.readValue(resource, Problem.class);
            throw new RuntimeException("Resource or file " + name + " not found.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Problem parseJson(String json) {
        try {
            return mapper.readValue(json, Problem.class);
        } catch (Exception ex){
            throw new RuntimeException("Json could not be parsed: " + json);
        }
    }

    public List<Submission> parseSubmissionText(String json) {
        try {
            return mapper.readValue(json,
                    TypeFactory.defaultInstance().constructCollectionType(List.class, Submission.class));
        } catch (Exception ex){
            throw new RuntimeException("Json could not be parsed: " + json);
        }
    }

    public List<Submission> parseSubmission(String name) {
        try {
            CollectionType type = TypeFactory.defaultInstance().constructCollectionType(List.class, Submission.class);
            File src = new File(name);
            if (src.exists())
                return mapper.readValue(src, type);
            InputStream resource = Parser.class.getResourceAsStream(name);
            if (resource!=null)
                return mapper.readValue(resource, type);
            throw new RuntimeException("Resource or file " + name + " not found.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
//        Problem p = new Parser().parse("/problems/problem_5.json");

//        System.out.println(p);
        List<Submission> subms = new Parser().parseSubmission("/submissions.json");
        System.out.println(subms);
    }
}
