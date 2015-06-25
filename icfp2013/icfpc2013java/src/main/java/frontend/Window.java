package frontend;

/**
 *        cpuWindow: {
         resetsIn: number;
         amount: number;
         limit: number
       };

 */
public class Window {
    private int resetsIn;
    private int amount;
    private int limit;

    public int getResetsIn() {
        return resetsIn;
    }

    public void setResetsIn(int resetsIn) {
        this.resetsIn = resetsIn;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "Window{" +
                "resetsIn=" + resetsIn +
                ", amount=" + amount +
                ", limit=" + limit +
                '}';
    }
}
