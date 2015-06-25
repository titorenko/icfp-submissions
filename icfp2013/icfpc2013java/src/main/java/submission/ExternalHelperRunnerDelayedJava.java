package submission;

import java.io.IOException;
import java.util.*;

/**
 *
 */
public class ExternalHelperRunnerDelayedJava implements ExternalHelperRunner {

    public static final String JAVA_HOME = "/usr/java/default";
//    private static final String JAVA_EXECUTABLE = "./test.sh";
    private static final String JAVA_EXECUTABLE = "/usr/java/default/bin/java";
//    private static final String JAVA_EXECUTABLE = "java";

    private Timer timer;
    private TimerTask launchTask;

    private final boolean inheritIO;
    private final String javaClass;
    private final String[] extras;
    private final String runnerId;
    private final String memory;
    private final long delay;

    private Process helperProcess;
    private String taskId;
    private int exitCode;

    public static class Builder {
        private boolean inheritIO = true;
        private String javaClass;
        private String[] extras;
        private String runnerId;
        private String memory;
        private long delay;

        public Builder(String id, String javaClass, long delay) {
            this.runnerId = id;
            this.javaClass = javaClass;
            this.delay = delay;
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
        public ExternalHelperRunnerDelayedJava build() {
            return new ExternalHelperRunnerDelayedJava(this);
        }
    }

    public ExternalHelperRunnerDelayedJava(Builder builder) {
        this.runnerId = builder.runnerId;
        this.memory = builder.memory;
        this.inheritIO = builder.inheritIO;
        this.javaClass = builder.javaClass;
        this.extras = builder.extras;
        this.delay = builder.delay;
        this.timer = new Timer(runnerId+"-delay", true);
    }

    @Override
    public void startProcess(String id) {
        taskId = id;
        exitCode = Integer.MIN_VALUE;
        launchTask = new TimerTask() {
            @Override
            public void run() {
                startProcess();
            }
        };
        timer.schedule(launchTask, delay);
    }

    private synchronized void startProcess() {
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
            System.out.println("Started external helper " + runnerId);
        } catch (IOException e) {
            System.out.println("Failed to start external helper " + runnerId);
        }
    }

    @Override
    public synchronized void abortProcess() {
        if (!launchTask.cancel()) {
            // we already started, terminate process
            helperProcess.destroy();
        }
    }

    @Override
    public synchronized void ensureProcessRuns() {
        if (launchTask.cancel()) {
            startProcess();
        }
        try {
            exitCode = helperProcess.waitFor();
        } catch (InterruptedException e) {
        }
    }

    @Override
    public synchronized boolean isFinished() {
        if (helperProcess==null) return false;
        try {
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
        ExternalHelperRunnerDelayedJava helperRunner =
                new Builder("RunnerId", "solver.SolverMainBonus", 5000)
//                        .memory("-Xmx4G")
                        .inheritIo(true)
                        .build();
        helperRunner.startProcess("02GJ1A32WAXH3JbazT1FrJu6");
        System.out.println(helperRunner.isFinished());
//        helperRunner.ensureProcessRuns();
//        Thread.sleep(6000);
//        helperRunner.abortProcess();
        while(!helperRunner.isFinished()) {
            System.out.println("Waiting...");
            Thread.sleep(1000);
        }
        System.out.println(helperRunner.succeeded());
//        Thread.sleep(5000);
    }
}
