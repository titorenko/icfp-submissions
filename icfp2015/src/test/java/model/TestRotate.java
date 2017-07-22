package model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestRotate {

    @Test
    public void testRotatePoint1() {
        Point p = Geometry.rotateCW(new Point(2,0), new Point(0,0));
        assertThat(p, is(new Point(1,2)));
    }

    @Test
    public void testRotatePoint2() {
        Point p = Geometry.rotateCW(new Point(1,1), new Point(0,0));
        assertThat(p, is(new Point(0,2)));
    }

    @Test
    public void testRotatePoint3() {
        Point p = Geometry.rotateCW(new Point(-3,3), new Point(0,0));
        assertThat(p, is(new Point(-4,-1)));
    }

    @Test
    public void testRotatePoint4() {
        Point p = Geometry.rotateCW(new Point(0,-2), new Point(0,0));
        assertThat(p, is(new Point(1,-1)));
    }
}
