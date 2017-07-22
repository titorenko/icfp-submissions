package icfp.io.model;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import model.Geometry;
import model.Point;
import model.UnitTemplate;

public class Unit {
    public List<Cell> members;
    public Cell pivot;


    public UnitTemplate createTemplate(int fieldWidth) {
        // build points for all rotations
        List<List<Point>> rotations = getPossibleRotations();

        // calculate first figure offset
        IntSummaryStatistics baseXStats = rotations.get(0).stream().mapToInt(p -> p.x).summaryStatistics();
        IntSummaryStatistics baseYStats = rotations.get(0).stream().mapToInt(p -> p.y).summaryStatistics();
        int sum = fieldWidth-baseXStats.getMax()-baseXStats.getMin();
        int offsetX = (sum-1)/2;
        int offsetY = -baseYStats.getMin();

        return new UnitTemplate(rotations, offsetX, offsetY);
    }

    private List<List<Point>> getPossibleRotations() {
        Point pivot = this.pivot.toPoint();
        List<List<Point>> rotations = new ArrayList<>();
        List<Point> constellation = members.stream()
                .map(Cell::toPoint)
                .collect(Collectors.toList());
        rotations.add(constellation);
        for(int i=0;i<5;i++) {
            constellation = constellation.stream()
                    .map(p -> Geometry.rotateCW(p, pivot))
                    .collect(Collectors.toList());
            rotations.add(constellation);
        }
        for(int i=0;i<6;i++) {
            List<Point> odd = rotations.get(i).stream()
                    .map(p -> p.y % 2 == 0 ? p : new Point(p.x+1,p.y))
                    .collect(Collectors.toList());
            rotations.add(odd);
        }
        return rotations;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "members=" + members +
                ", pivot=" + pivot +
                '}';
    }
}
