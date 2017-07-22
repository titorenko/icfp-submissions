package icfp.io.model;

import model.Geometry;
import model.UnitTemplate;
import org.junit.Test;

import java.util.ArrayList;

public class TestUnitTemplate {

    @Test
    public void testTemplates() {
        Unit unit = buildUnit(
                        "....\n" +
                        "....\n" +
                        "..*.\n" +
                        "..*.",
                        1,1
        );
        UnitTemplate template = unit.createTemplate(10);
        System.out.println("Offs X: " + template.getOffsetX());
        System.out.println("Offs Y: " + template.getOffsetY());
        for(int i=0;i<6;i++) {
            System.out.println(i);
            System.out.println(Geometry.fromPoints(template.setFor(0, i).getPoints(), 0, 0));
        }

    }

    @Test
    public void testTemplates2() {
        Unit unit = buildUnit(
                        "******..\n" +
                        "........\n" +
                        "........\n" +
                        "........",
                        1,1
        );
        UnitTemplate template = unit.createTemplate(10);
        System.out.println("Offs X: " + template.getOffsetX());
        System.out.println("Offs Y: " + template.getOffsetY());
        for(int i=0;i<6;i++) {
            System.out.println(i);
            System.out.println(Geometry.fromPoints(template.setFor(0, i).getPoints(), 0, 0));
        }

    }

    private Unit buildUnit(String body, int pivotX, int pivotY) {
        Unit unit = new Unit();
        unit.members = new ArrayList<>();
        unit.pivot = new Cell(pivotX, pivotY);
        String[] lines = body.split("\n");
        for(int i=0;i<lines.length;i++) {
            String line = lines[i];
            for(int j=0;j<line.length();j++) {
                if (line.charAt(j)=='*') {
                    unit.members.add(new Cell(j,i));
                }
            }
        }
        return unit;
    }
}
