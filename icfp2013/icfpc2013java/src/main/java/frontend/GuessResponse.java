package frontend;

import java.util.Arrays;

/**
 *interface GuessResponse {
      status: string;
      values?: string[];
      message?: string;
      lightning?: bool;
    }

 */
public class GuessResponse {
    private GuessResponseStatus status;
    private String[] values; //
    private String message; // error message
    private Boolean lightning; // this success was counted as lightning division

    public GuessResponseStatus getStatus() {
        return status;
    }

    public void setStatus(GuessResponseStatus status) {
        this.status = status;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getLightning() {
        return lightning;
    }

    public void setLightning(Boolean lightning) {
        this.lightning = lightning;
    }

    public String failedInput() {
        return values!=null?values[0]:null;
    }

    public String correctOutput() {
        return values!=null?values[1]:null;
    }

    public String incorrectOutput() {
        return values!=null?values[2]:null;
    }

    @Override
    public String toString() {
        return "GuessResponse{" +
                "status=" + status +
                ", values=" + Arrays.toString(values) +
                ", message='" + message + '\'' +
                ", lightning=" + lightning +
                '}';
    }
}
