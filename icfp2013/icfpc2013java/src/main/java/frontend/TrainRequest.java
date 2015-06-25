package frontend;

import java.util.Arrays;

/**
 * interface TrainRequest {
     size?: number;
     operators?: string[];
    }

 */
public class TrainRequest {
    private int size;
    private String[] operators;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String[] getOperators() {
        return operators;
    }

    public void setOperators(String[] operators) {
        this.operators = operators;
    }

    public static TrainRequest request(int size, String ... ops) {
        TrainRequest tr = new TrainRequest();
        tr.setSize(size);
        tr.setOperators(ops);
        return tr;
    }

    @Override
    public String toString() {
        return "TrainRequest{" +
                "size=" + size +
                ", operators=" + Arrays.toString(operators) +
                '}';
    }
}
