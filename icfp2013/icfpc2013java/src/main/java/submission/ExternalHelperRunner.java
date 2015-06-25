package submission;

/**
 *
 */
public interface ExternalHelperRunner {

    void startProcess(String id);

    void abortProcess();

    void ensureProcessRuns();

    boolean isFinished();

    boolean succeeded();

    String getHelperId();
}
