package frontend;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *   interface EvalResponse {
      status: string;
      outputs?: string[];
      message?: string;
    }

 */
public class EvalResponse {
    private EvalResponseStatus status; // ok or error
    private String[] outputs;
    private String message;

    public EvalResponseStatus getStatus() {
        return status;
    }

    public void setStatus(EvalResponseStatus status) {
        this.status = status;
    }

    public String[] getOutputs() {
        return outputs;
    }

    @JsonIgnore
    public List<BigInteger> getNumericOutputs() {
        List<BigInteger> result = new ArrayList<>();
        for (String str : outputs) {
            if (str.startsWith("0x")) {
                str = str.substring(2);
            }
            result.add(new BigInteger(str, 16));
        }
        return result;
    }

    public void setOutputs(String[] outputs) {
        this.outputs = outputs;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "EvalResponse{" +
                "status='" + status + '\'' +
                ", outputs=" + Arrays.toString(outputs) +
                ", message='" + message + '\'' +
                '}';
    }
}
