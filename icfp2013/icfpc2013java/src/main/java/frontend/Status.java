package frontend;

/**
 * interface Status {
      easyChairId: number;
      contestScore: number;
      lightningScore: number;
      trainingScore: number;
      mismatches: number;
      numRequests: number;
      requestWindow: {
        resetsIn: number;
        amount: number;
        limit: number
      };
      cpuWindow: {
        resetsIn: number;
        amount: number;
        limit: number
      };
      cpuTotalTime:number;
    }

 */
public class Status {
    private int easyChairId;
    private int contestScore;
    private int lightningScore;
    private int trainingScore;
    private int mismatches;
    private int numRequests;
    private Window requestWindow;
    private Window cpuWindow;
    private int cpuTotalTime;

    public int getEasyChairId() {
        return easyChairId;
    }

    public void setEasyChairId(int easyChairId) {
        this.easyChairId = easyChairId;
    }

    public int getContestScore() {
        return contestScore;
    }

    public void setContestScore(int contestScore) {
        this.contestScore = contestScore;
    }

    public int getLightningScore() {
        return lightningScore;
    }

    public void setLightningScore(int lightningScore) {
        this.lightningScore = lightningScore;
    }

    public int getTrainingScore() {
        return trainingScore;
    }

    public void setTrainingScore(int trainingScore) {
        this.trainingScore = trainingScore;
    }

    public int getMismatches() {
        return mismatches;
    }

    public void setMismatches(int mismatches) {
        this.mismatches = mismatches;
    }

    public int getNumRequests() {
        return numRequests;
    }

    public void setNumRequests(int numRequests) {
        this.numRequests = numRequests;
    }

    public Window getRequestWindow() {
        return requestWindow;
    }

    public void setRequestWindow(Window requestWindow) {
        this.requestWindow = requestWindow;
    }

    public Window getCpuWindow() {
        return cpuWindow;
    }

    public void setCpuWindow(Window cpuWindow) {
        this.cpuWindow = cpuWindow;
    }

    public int getCpuTotalTime() {
        return cpuTotalTime;
    }

    public void setCpuTotalTime(int cpuTotalTime) {
        this.cpuTotalTime = cpuTotalTime;
    }

    @Override
    public String toString() {
        return "Status{" +
                "easyChairId=" + easyChairId +
                ", contestScore=" + contestScore +
                ", lightningScore=" + lightningScore +
                ", trainingScore=" + trainingScore +
                ", mismatches=" + mismatches +
                ", numRequests=" + numRequests +
                ", requestWindow=" + requestWindow +
                ", cpuWindow=" + cpuWindow +
                ", cpuTotalTime=" + cpuTotalTime +
                '}';
    }
}
