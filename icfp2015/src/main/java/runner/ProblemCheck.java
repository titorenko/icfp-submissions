package runner;

import icfp.io.Parser;
import icfp.io.model.Problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class ProblemCheck {
    public static class SizeRotations {
        public final int size;
        public final int rotations;

        public SizeRotations(int size, int rotations) {
            this.size = size;
            this.rotations = rotations;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SizeRotations that = (SizeRotations) o;

            if (size != that.size) return false;
            return rotations == that.rotations;

        }

        @Override
        public int hashCode() {
            int result = size;
            result = 31 * result + rotations;
            return result;
        }

        @Override
        public String toString() {
            return  "{ size=" + size +
                    ", rotations=" + rotations + " }";
        }
    }

    public static void main(String[] args) {
        List<Problem> problems = new ArrayList<>();
        Parser parser = new Parser();
        for(int i=0;i<25;i++) {
            problems.add(parser.parse("/problems/problem_" + i + ".json"));
        }
        problems.forEach(p -> {
                    System.out.println(
                            String.format("id %d, w %d, h %d, fill %d, seeds %d, units %d",
                                    p.id, p.width, p.height, p.filled.size(), p.sourceSeeds.length, p.units.length));
                    System.out.println(
                            Arrays.stream(p.units)
                                    .collect(
                                            Collectors.groupingBy(unit ->
                                                            new SizeRotations(
                                                                    unit.members.size(),
                                                                    unit.createTemplate(p.width).getAllowedAngles().size()),
                                                    Collectors.counting()
                                            )
                                    )
                    );
                }
        );
    }
}
