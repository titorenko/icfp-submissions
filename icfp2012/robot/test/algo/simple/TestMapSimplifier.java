package algo.simple;

import model.Mine;
import model.MineFactory;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestMapSimplifier {

    @Test
    public void testSimpleCase1() {
        String minespec =
                "###\n"+
                "#.#\n"+
                "###\n";
        Mine mine = MineFactory.getMine(minespec);
        assertTrue(new MapSimplifier(mine).canBeCleared(1, mine));
    }

    @Test
    public void testSimpleCase2() {
        String minespec =
                "###\n"+
                "#.#\n"+
                "#.#\n"+
                "###\n";
        Mine mine = MineFactory.getMine(minespec);
        assertTrue(new MapSimplifier(mine).canBeCleared(1, mine));
        assertTrue(new MapSimplifier(mine).canBeCleared(2, mine));
    }

    @Test
    public void testSimpleCase3() {
        String minespec =
                "####\n"+
                "#..#\n"+
                "#..#\n"+
                "####\n";
        Mine mine = MineFactory.getMine(minespec);
        assertTrue(new MapSimplifier(mine).canBeCleared(1, mine));
    }

    @Test
    public void testSimpleCase4() {
        String minespec =
                "#########\n"+
                "#.......#\n"+
                "#.#..#..#\n"+
                "#########\n";
        Mine mine = MineFactory.getMine(minespec);
        assertTrue(new MapSimplifier(mine).canBeCleared(1, mine));
        assertTrue(new MapSimplifier(mine).canBeCleared(2, mine));
    }

    @Test
    public void testSimpleCase5() {
        String minespec =
                "#########\n"+
                "#..#.#..#\n"+
                "#.....#.#\n"+
                "#.......#\n"+
                "#########\n";
        Mine mine = MineFactory.getMine(minespec);
        assertTrue(new MapSimplifier(mine).canBeCleared(1, mine));
    }

    @Test
    public void testSimpleCase6() {
        String minespec =
                "#########\n"+
                "#..#.#..#\n"+
                "#.....#.#\n"+
                "#.......#\n"+
                "#########\n";
        Mine mine = MineFactory.getMine(minespec);
        assertTrue(new MapSimplifier(mine).canBeCleared(2, mine));
    }

    @Test
    public void testSimpleCaseF1() {
        String minespec =
                "###\n"+
                "#*#\n"+
                "#.#\n"+
                "###\n";
        Mine mine = MineFactory.getMine(minespec);
        assertFalse(new MapSimplifier(mine).canBeCleared(1, mine));
    }

    @Test
    public void testSimpleCaseF2() {
        String minespec =
                "#####\n"+
                "#...#\n"+
                "#.*.#\n"+
                "#####\n";
        Mine mine = MineFactory.getMine(minespec);
        assertFalse(new MapSimplifier(mine).canBeCleared(1, mine));
    }

    @Test
    public void testSimpleCaseF3() {
        String minespec =
                "####\n"+
                "#*.#\n"+
                "#..#\n"+
                "####\n";
        Mine mine = MineFactory.getMine(minespec);
        assertFalse(new MapSimplifier(mine).canBeCleared(1, mine));
    }

    @Test
    public void testSimpleCaseF4() {
        String minespec =
                "#########\n"+
                "#..#.#.*.#\n"+
                "#.....##.#\n"+
                "#........#\n"+
                "##########\n";
        Mine mine = MineFactory.getMine(minespec);
        assertFalse(new MapSimplifier(mine).canBeCleared(1, mine));
    }

    @Test
    public void testSimpleCase7() {
        String minespec =
                "#########\n"+
                "#..#.#*.#\n"+
                "#.....#.#\n"+
                "#.......#\n"+
                "#########\n";
        Mine mine = MineFactory.getMine(minespec);
        assertFalse(new MapSimplifier(mine).canBeCleared(1, mine));//can be made true
    }

    @Test
    public void testSimpleCase8() {
        String minespec =
                "#########\n"+
                "#...*...#\n"+
                "#..####.#\n"+
                "#..#..#.#\n"+
                "#..#..#.#\n"+
                "#.......#\n"+
                "#########\n";
        Mine mine = MineFactory.getMine(minespec);
        assertFalse(new MapSimplifier(mine).canBeCleared(2, mine));//can be made true
    }

   
    @Test
    public void testRealMap() {
        Mine mine = MineFactory.getMineFromResource("/contest10.map.txt");
        Mine simplified = null;
        long startTime = System.currentTimeMillis();
        for(int i=0;i<100;i++) {
            simplified = new MapSimplifier(mine).wipeEarth();
        }
        System.out.println("Simplified in " + (System.currentTimeMillis()-startTime) + " ms");
        System.out.println(simplified);
    }
}
