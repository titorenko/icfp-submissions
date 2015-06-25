package frontend;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 *           interface EvalRequest {
     id?: string;
     program?: string;
     arguments: string[];
    }

 */
public class EvalRequest implements ProblemRequest {
    private String id;
    private String program;
    private String[] arguments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String[] getArguments() {
        return arguments;
    }

    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    public static EvalRequest programRequest(String program, String[] data) {
        EvalRequest request = new EvalRequest();
        request.setProgram(program);
        request.setArguments(data);
        return request;
    }

    public static EvalRequest programRequest(String program, List<BigInteger> data) {
        return programRequest(program, Lists.transform(data, new Function<BigInteger, String>() {
            @Override
            public String apply(BigInteger input) {
                return "0x" + input.toString(16);
            }
        }).toArray(new String [data.size()]));
    }

    public static EvalRequest programRequestById(String id, String[] data) {
        EvalRequest request = new EvalRequest();
        request.setId(id);
        request.setArguments(data);
        return request;
    }

    public static EvalRequest programRequestById(String program, List<BigInteger> data) {
        return programRequestById(program, Lists.transform(data, new Function<BigInteger, String>() {
            @Override
            public String apply(BigInteger input) {
                return "0x" + input.toString(16);
            }
        }).toArray(new String [data.size()]));
    }

    @Override
    public String toString() {
        return "EvalRequest{" +
                "id='" + id + '\'' +
                ", program='" + program + '\'' +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }
}
