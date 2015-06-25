package submission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class ExternalHelperRunnerJava implements ExternalHelperRunner {

    private static final String HELPER_PROCESS_NAME = "solver.SolverMain";
    public static final String JAVA_HOME = "/usr/java/default";
//    private static final String JAVA_EXECUTABLE = "./test.sh";
    private static final String JAVA_EXECUTABLE = "/usr/java/default/bin/java";
//    private static final String JAVA_EXECUTABLE = "java";

    private final boolean inheritIO;
    private final String javaClass;
    private final String[] extras;
    private final String runnerId;
    private final String memory;

    private Process helperProcess;
    private String taskId;
    private int exitCode;

    public static class Builder {
        private boolean inheritIO = true;
        private String javaClass;
        private String[] extras;
        private String runnerId;
        private String memory;

        public Builder(String id, String javaClass) {
            this.runnerId = id;
            this.javaClass = javaClass;
        }
        public Builder inheritIo(boolean inheritIO) {
            this.inheritIO = inheritIO;
            return this;
        }
        public Builder memory(String memory) {
            this.memory = memory;
            return this;
        }
        public Builder extraArgs(String ... extras) {
            this.extras = extras;
            return this;
        }
        public ExternalHelperRunnerJava build() {
            return new ExternalHelperRunnerJava(runnerId, javaClass, memory, inheritIO, extras);
        }
    }

    public ExternalHelperRunnerJava(String runnerId, String javaClass, String memory, boolean inheritIO, String... extras) {
        this.runnerId = runnerId;
        this.memory = memory;
        this.inheritIO = inheritIO;
        this.javaClass = javaClass;
        this.extras = extras;
    }

    private void startProcess() {
        ProcessBuilder builder = new ProcessBuilder();
        if (System.getenv("JAVA_HOME)")==null) {
            builder.environment().put("JAVA_HOME", JAVA_HOME);
        }
        List<String> cmdLine = new ArrayList<>();
        cmdLine.add(JAVA_EXECUTABLE);
        if (memory!=null) cmdLine.add(memory);
        cmdLine.add("-classpath");
        cmdLine.add(System.getProperty("java.class.path"));
        cmdLine.add(javaClass);
        cmdLine.add(taskId);
        if (extras!=null) cmdLine.addAll(Arrays.asList(extras));
        builder.command(cmdLine);
        if (inheritIO) builder.inheritIO();
        try {
            helperProcess = builder.start();
            System.out.println("Started external helper " + runnerId+" at "+new Date());
        } catch (IOException e) {
            System.out.println("Failed to start external helper " + runnerId);
        }
    }

    @Override
    public void startProcess(String id) {
        taskId = id;
        exitCode = Integer.MIN_VALUE;
        startProcess();
    }

    @Override
    public void abortProcess() {
        helperProcess.destroy();
        helperProcess = null;
    }

    @Override
    public void ensureProcessRuns() {
        try {
            if (helperProcess!=null)
                exitCode = helperProcess.waitFor();
            helperProcess = null;
        } catch (InterruptedException e) {
        }
    }

    @Override
    public boolean isFinished() {
        try {
            if (helperProcess==null) return true;
            exitCode = helperProcess.exitValue();
            return true;
        } catch (IllegalThreadStateException ex) {
            return false;
        }
    }

    @Override
    public boolean succeeded() {
        return exitCode==0;
    }

    @Override
    public String getHelperId() {
        return runnerId;
    }

    public static void main(String[] args) throws Exception {
        ExternalHelperRunner helperRunner =
                new ExternalHelperRunnerJava("RunnerId", "solver.SolverMain", "-Xmx4G", true, HELPER_PROCESS_NAME);
        helperRunner.startProcess("02GJ1A32WAXH3JbazT1FrJu6");
        System.out.println(helperRunner.isFinished());
        helperRunner.ensureProcessRuns();
        System.out.println(helperRunner.isFinished());
        System.out.println(helperRunner.succeeded());
    }
}
