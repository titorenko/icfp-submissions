package util;

import frontend.Problem;

import java.util.Arrays;

/**
 *
 */
public class SortTasks {

    public static void main(String[] args) {
        Problem[] problems = Util.getAllProblems();

        Arrays.sort(problems, new SizeComparator());

        for(Problem problem : problems) {
            System.out.println(problem);
        }
    }
}
