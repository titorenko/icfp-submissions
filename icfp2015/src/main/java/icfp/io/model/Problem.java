package icfp.io.model;

import com.google.common.collect.ImmutableList;
import model.Board;
import model.Random;
import model.StaticBoardInfo;
import model.UnitTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Stream;

public class Problem {
    public int id;
    public Unit[] units;
    public int width;
    public int height;
    public List<Cell> filled;
    public int sourceLength;
    public int sourceSeeds[];

    public List<model.Board> createBoards() {
        BitSet bits = boardBits();

        List<model.Board> boards = new ArrayList<>();
        for(int seed : sourceSeeds) {
            boards.add(Board.initialBoard(
                    (BitSet) bits.clone(),
                    new StaticBoardInfo(width, height, ImmutableList.copyOf(unitsForSeed(seed)))));
        }

        return boards;
    }

    public Stream<BoardInfo> createBoardStream() {
        BitSet bits = boardBits();
        return Arrays.stream(sourceSeeds).mapToObj(seed ->
                new BoardInfo(
                        id,
                        seed,
                        Board.initialBoard(
                                (BitSet) bits.clone(),
                                new StaticBoardInfo(width, height, ImmutableList.copyOf(unitsForSeed(seed))))));
    }

    private List<UnitTemplate> unitsForSeed(int seed) {
        Random random = new Random(seed);
        List<UnitTemplate> source = new ArrayList<>();

        for(int i=0;i<sourceLength;i++) {
            int nextSource = random.next()%units.length;
            source.add(units[nextSource].createTemplate(width));
        }
        return source;
    }

    private BitSet boardBits() {
        BitSet bs = new BitSet(width*height);
        for(Cell cell : filled) {
            bs.set(cell.cellIndex(width));
        }
        return bs;
    }

    public int boardSpace() {
        return width*height;
    }

    public int sourcesSize() {
        return sourceSeeds.length;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "id=" + id +
                ", units=" + Arrays.toString(units) +
                ", width=" + width +
                ", height=" + height +
                ", filled=" + filled +
                ", sourceLength=" + sourceLength +
                ", sourceSeeds=" + Arrays.toString(sourceSeeds) +
                '}';
    }
}
