package parser;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;

import model.Mine;

import org.junit.Test;

public class TestParser {

    private String eol = System.getProperty("line.separator");

    private String map1 = "######" + eol +
            "#. *R#" + eol +
            "#  \\.#" + eol +
            "#\\ * #" + eol +
            "L  .\\#" + eol +
            "######" + eol;

    @Test
    public void testMap1() {
        testParsing(map1);
    }

    private String map2 = "#######" + eol +
            "#..***#" + eol +
            "#..\\\\\\#" + eol +
            "#...**#" + eol +
            "#.*.*\\#" + eol +
            "LR....#" + eol +
            "#######" + eol;

    @Test
    public void testMap2() {
        testParsing(map2);
    }

    private String map3 = "########" + eol +
            "#..R...#" + eol +
            "#..*...#" + eol +
            "#..#...#" + eol +
            "#.\\.\\..L" + eol +
            "####**.#" + eol +
            "#\\.....#" + eol +
            "#\\..* .#" + eol +
            "########" + eol;

    @Test
    public void testMap3() {
        testParsing(map3);
    }
    private String map4 = "#########" + eol +
            "#.*..#\\.#" + eol +
            "#.\\..#\\.L" + eol +
            "#.R .##.#" + eol +
            "#.\\  ...#" + eol +
            "#..\\  ..#" + eol +
            "#...\\  ##" + eol +
            "#....\\ \\#" + eol +
            "#########" + eol;

    @Test
    public void testMap4() {
        testParsing(map4);
    }
    private String map5 = "############" + eol +
            "#..........#" + eol +
            "#.....*....#" + eol +
            "#..\\\\\\\\\\\\..#" + eol +
            "#.     ....#" + eol +
            "#..\\\\\\\\\\\\\\.#" + eol +
            "#..\\..    .#" + eol +
            "#..\\.. ....#" + eol +
            "#..... ..* #" + eol +
            "#..### ### #" + eol +
            "#...R#\\#\\\\.#" + eol +
            "######L#####" + eol;

    @Test
    public void testMap5() {
        testParsing(map5);
    }
    private String map6 = "###############" + eol +
            "#\\\\\\.......** #" + eol +
            "#\\\\#.#####...##" + eol +
            "#\\\\#.....*##. #" + eol +
            "#\\#####\\...## #" + eol +
            "#\\......####* #" + eol +
            "#\\.######* #.\\#" + eol +
            "#\\.#. *...##.##" + eol +
            "#\\##. ..  *...#" + eol +
            "#\\...... L#.#.#" + eol +
            "###########.#.#" + eol +
            "#\\..........#.#" + eol +
            "##.##########.#" + eol +
            "#R.#\\.........#" + eol +
            "###############" + eol;

    @Test
    public void testMap6() {
        testParsing(map6);
    }
    private String map7 = "    #######        " + eol +
            "    ##    *#       " + eol +
            "     ##R  *##      " + eol +
            "      ##\\\\\\\\##     " + eol +
            "       ##....##    " + eol +
            "      ##..\\ . ##   " + eol +
            "     ## . L .  ##  " + eol +
            "    ##\\\\\\# #\\\\\\\\## " + eol +
            "   ######   #######" + eol;

    @Test
    public void testMap7() {
        testParsing(map7);
    }
    private String map8 = "##############       " + eol +
            "#\\\\... ......#       " + eol +
            "###.#. ...*..#       " + eol +
            "  #.#. ... ..#       " + eol +
            "### #.   \\ ..#       " + eol +
            "#. .#..... **####### " + eol +
            "#.#\\#..... ..\\\\\\*. # " + eol +
            "#*\\\\#.###. ####\\\\\\ # " + eol +
            "#\\\\.#.     ...## \\ # " + eol +
            "#\\#.#..... ....# \\ # " + eol +
            "###.#..... ....#   ##" + eol +
            "#\\\\.#..... ....#\\   #" + eol +
            "########.. ..###*####" + eol +
            "#......... .........#" + eol +
            "#......... ....***..#" + eol +
            "#..\\\\\\\\\\ # ####.....#" + eol +
            "#........*R..\\\\\\   .#" + eol +
            "##########L##########" + eol;

    @Test
    public void testMap8() {
        testParsing(map8);
    }
    private String map9 = "        #L#######         " + eol +
            "        #*** \\\\ #         " + eol +
            "        #\\\\\\ .. #         " + eol +
            "#########.##    ##########" + eol +
            "#.......\\ ..........*   .#" + eol +
            "#*******\\......#....#\\\\ .#" + eol +
            "###\\.\\\\\\...**..#....... *#" + eol +
            "#*****\\\\  .\\\\..##     #\\.#" + eol +
            "######### ....  ##########" + eol +
            "        #       #         " + eol +
            "        ####*####         " + eol +
            "        #.......#         " + eol +
            "#########  \\\\\\\\*##########" + eol +
            "#*\\\\  **#     *..*\\ \\\\\\\\\\#" + eol +
            "#.\\**\\*** .....**.# \\\\##\\#" + eol +
            "#\\R......     .\\\\.. \\\\\\\\\\#" + eol +
            "##########################" + eol;

    @Test
    public void testMap9() {
        testParsing(map9);
    }
    private String map10 = "#############################" + eol +
            "#..........................\\#" + eol +
            "#..\\\\###...#....        ###.#" + eol +
            "#..\\*\\\\\\.. #.... ..##\\\\..\\#.#" + eol +
            "#..\\*\\.... #.... ..#\\#....#.#" + eol +
            "#...\\###.. #.... ....#....#.#" + eol +
            "#... ..... ..... .####......#" + eol +
            "#\\\\. #....           .......#" + eol +
            "#... #..#. .....*\\ ##.......#" + eol +
            "#.#....... ...#..  ....######" + eol +
            "#. ...#... ...#.\\  ....#..* #" + eol +
            "##........ ...#.. #....#.#\\\\#" + eol +
            "#.....*... .....*\\#\\\\.....*.#" + eol +
            "#.***.* .......*\\****.....#.#" + eol +
            "#.\\\\\\.. ................   .#" + eol +
            "#.#####    .######    ##### #" + eol +
            "#....\\\\.................... #" + eol +
            "#....****...#.##.....\\\\\\\\..\\#" + eol +
            "#....\\\\\\\\...#.........*....\\#" + eol +
            "#....\\\\\\\\...#.\\\\.    #\\###.\\#" + eol +
            "#....     ..#.... ...#\\\\\\\\. #" + eol +
            "#........ ..#.... ...#..... #" + eol +
            "#........         ........#R#" + eol +
            "###########################L#" + eol;

    @Test
    public void testMap10() {
        testParsing(map10);
    }

    private void testParsing(String map) {
        MapParser.MineInfo info = new MapParser(new StreamInput(new StringReader(map))).readMap();
        Mine mine = new Mine(info.width, info.cells);
        StringWriter writer = new StringWriter();
        new MapPrinter(new StreamOutput(writer)).printMap(mine);
        assertEquals("Map", map, writer.toString());
    }

    private String map1x = "####" + eol +
            "#. *R#" + eol +
            "#  \\.#" + eol +
            "#\\ *#" + eol +
            "L  .\\#" + eol +
            "######" + eol;

    private String map1t = "####  " + eol +
            "#. *R#" + eol +
            "#  \\.#" + eol +
            "#\\ *# " + eol +
            "L  .\\#" + eol +
            "######" + eol;

    @Test
    public void testLinePadding() {
        MapParser.MineInfo info = new MapParser(new StreamInput(new StringReader(map1x))).readMap();
        Mine mine = new Mine(info.width, info.cells);
        StringWriter writer = new StringWriter();
        new MapPrinter(new StreamOutput(writer)).printMap(mine);
        assertEquals("Map", map1t, writer.toString());
    }

    @Test
    public void testTrampolines() {
        String trampoMap =
                        "###########################" + eol +
                        "#   A            2        #" + eol +
                        "#     C    1              #" + eol +
                        "#                         #" + eol +
                        "#       D        4 I      #" + eol +
                        "###########################" + eol;
        testParsing(trampoMap);
    }
}
