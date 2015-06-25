package util;

import frontend.Problem;

import java.util.Comparator;

/**
*
*/
public class SizeComparator implements Comparator<Problem> {
    @Override
    public int compare(Problem o1, Problem o2) {
        return o1.getSize()-o2.getSize();
    }
}
