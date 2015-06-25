package submission;

import domain.Operator;
import frontend.Frontend;
import frontend.Problem;
import util.SizeComparator;
import util.TaskFilter;

import java.io.File;
import java.util.*;

import com.google.common.base.Preconditions;

/**
 * Start many external processes to solve and wait for the fastest to succeed,
 * terminate others
 */
public class Multisolver {

    private static Frontend frontend = new Frontend();

    private static class HelperStat {
        public String name;
        public int success;
        public int failed;
        public int runned;

        @Override
        public String toString() {
            return  "" + name +
                    " r=" + runned +
                    " s=" + success +
                    " f=" + failed;
        }
    }

    private static Map<String, HelperStat> stats = new HashMap<String, HelperStat>();

    public static void solveMultitask(String idToRun) {
        List<ExternalHelperRunner> runners = new ArrayList<ExternalHelperRunner>();
//        runners.add(new ExternalHelperRunnerDelayedJava.Builder("Runner Sergey     ", "solver.SolverMain", 30000)
//                .memory("-Xmx4G")
//                .build());
//        runners.add(new ExternalHelperRunnerDelayedJava.Builder("Runner Bonus      ", "solver.SolverMainBonus", 60000)
//                .memory("-Xmx4G")
//                .build());
        runners.add(new ExternalHelperRunnerJava.Builder("Runner Balanced   ", "submission.Submission")
                .memory("-Xmx8G")
                .extraArgs("PESSIMISTIC", "true")
                .build());
   //     runners.add(new ExternalHelperRunnerJava.Builder("Runner Pessimistic", "submission.Submission")
    //            .memory("-Xmx4G")
    //            .extraArgs("BALANCED", "false")
   //             .build());
    //    runners.add(new ExternalHelperRunnerDelayedJava.Builder("Runner Aggressive ", "submission.Submission", 60000)
       //         .memory("-Xmx2G")
      //          .extraArgs("AGGRESSIVE", "false")
      //          .build());

        for(ExternalHelperRunner runner : runners) {
            runner.startProcess(idToRun);
            started(runner.getHelperId());
        }
        // wait for someone to finish
        List<ExternalHelperRunner> active = new ArrayList<>(runners);
        boolean success = false;
        do {
            Iterator<ExternalHelperRunner> iterator = active.iterator();
            while (iterator.hasNext()) {
                ExternalHelperRunner runner = iterator.next();

                if (runner.isFinished()) {
                    if (runner.succeeded()) {
                        success(runner.getHelperId());
                        success = true;
                    } else {
                        failed(runner.getHelperId());
                    }
                    iterator.remove();
                }
            }
            if (!success) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        } while(!success && !active.isEmpty());

        for(ExternalHelperRunner runner : runners) {
            runner.abortProcess();
        }

        if (!success) {
            System.out.println(idToRun + " failed on all solvers.");
        }
        dumpStats();
    }

    private static void dumpStats() {
        for(HelperStat stat : stats.values()) {
            System.out.println(stat);
        }
    }

    private static void started(String id) {
        HelperStat stat = stats.get(id);
        if (stat==null) {
            stat = new HelperStat();
            stat.name = id;
            stats.put(id, stat);
        }
        stat.runned++;
    }

    private static void success(String id) {
        HelperStat stat = stats.get(id);
        stat.success++;
    }

    private static void failed(String id) {
        HelperStat stat = stats.get(id);
        stat.failed++;
    }

    private static void doSubmissionTask(int maxTasks, int from, int to) {
        Collection<Problem> problems = getRemainingProblems(from, to);
        int of = Math.min(problems.size(), maxTasks);
        int n = 1;
        for (Iterator<Problem> iterator = problems.iterator(); iterator.hasNext() && n<=maxTasks; n++) {
            if (new File("terminate").exists()) {
                System.out.println("Terminating solver since 'terminate' file exists in current dir.");
                break;
            }
            Problem prob = iterator.next();
            System.out.println("Preparing problem " + n + "/" + of);
            solveMultitask(prob.getId());
        }
    }

    private static Collection<Problem> getRemainingProblems(int from, int to) {
        System.out.println("Fetching remaining tasks on levels [" + from + ',' + to + ']');
        TaskFilter filter = new TaskFilter(frontend.getProblems());
        filter.removeSolved();
        filter.excludeGreaterThan(to);
        filter.excludeLessThan(from);
        filter.excludeOperator(Operator.bonus.name());
//        filter.excludeOperator(Operator.fold.name());
//        filter.excludeOperator(Operator.tfold.name());
//        filter.excludeOperator(Operator.if0.name());
        filter.sort(new SizeComparator());
        return filter.getProblems();
    }

    // arguments
    //  id
    //  batch
    //  batch max
    //  batch min max
    public static void main(String[] args) {
    	Preconditions.checkArgument(args.length > 0);
    	String arg = args[0];
    	int nTasks = -1;
    	try {
    		nTasks = Integer.parseInt(arg);
    	} catch (Exception e) {
    		nTasks = -1;
    	}
    	if (nTasks>0) {
            int minLevel = 0;
            int maxLevel = Integer.MAX_VALUE;
            switch(args.length) {
                case 2:
                    maxLevel = Integer.parseInt(args[1]);
                    break;
                case 3:
                    minLevel = Integer.parseInt(args[1]);
                    maxLevel = Integer.parseInt(args[2]);
                    break;
            }
    		doSubmissionTask(nTasks, minLevel, maxLevel);
    	} else {
    		solveMultitask(arg);
    	}
    }
}
