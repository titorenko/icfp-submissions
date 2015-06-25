package util;

import frontend.Frontend;
import frontend.Problem;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.File;
import java.io.IOException;

public class FetchTasks {
    public static void main(String[] args) throws IOException {
        Frontend frontend = new Frontend();
        Problem problems[] = frontend.getProblems();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.writeValue(new File("problems"), problems);
    }
}
