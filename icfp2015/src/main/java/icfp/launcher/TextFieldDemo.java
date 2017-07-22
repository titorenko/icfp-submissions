package icfp.launcher;

import icfp.io.Parser;
import icfp.io.model.Problem;
import model.Board;
import model.MoveEncoding;
import model.Point;
import model.UnitTemplate;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Color;
import java.awt.Font;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.GroupLayout.*;

import static model.MoveEncoding.*;

public class TextFieldDemo extends JFrame {

    private static final Map<MoveEncoding, MoveEncoding> reverse = new HashMap<>();
    static {
        reverse.put(CCW, CW);
        reverse.put(CW, CCW);
        reverse.put(W, E);
        reverse.put(E, W);
    }

    private static final Map<Character, MoveEncoding> wordsToMoves = new HashMap<>();
    static {
        wordsToMoves.put('p', W);
        wordsToMoves.put('\'', W);
        wordsToMoves.put('!', W);
        wordsToMoves.put('.', W);
        wordsToMoves.put('0', W);
        wordsToMoves.put('3', W);
        wordsToMoves.put('b', E);
        wordsToMoves.put('c', E);
        wordsToMoves.put('e', E);
        wordsToMoves.put('f', E);
        wordsToMoves.put('y', E);
        wordsToMoves.put('2', E);
        wordsToMoves.put('a', SW);
        wordsToMoves.put('g', SW);
        wordsToMoves.put('h', SW);
        wordsToMoves.put('i', SW);
        wordsToMoves.put('j', SW);
        wordsToMoves.put('4', SW);
        wordsToMoves.put('l', SE);
        wordsToMoves.put('m', SE);
        wordsToMoves.put('n', SE);
        wordsToMoves.put('o', SE);
        wordsToMoves.put(' ', SE);
        wordsToMoves.put('5', SE);
        wordsToMoves.put('d', CW);
        wordsToMoves.put('q', CW);
        wordsToMoves.put('r', CW);
        wordsToMoves.put('v', CW);
        wordsToMoves.put('z', CW);
        wordsToMoves.put('1', CW);
        wordsToMoves.put('k', CCW);
        wordsToMoves.put('s', CCW);
        wordsToMoves.put('t', CCW);
        wordsToMoves.put('u', CCW);
        wordsToMoves.put('w', CCW);
        wordsToMoves.put('x', CCW);
    }

    private static final Set<Character> ignored = new HashSet<>();
    static {
        ignored.add('\t');
        ignored.add('\n');
        ignored.add('\r');
    }

    private final Board initialBoard;

    // sequence entry
    private JLabel prompt;
    private JTextField entry;
    private JButton execute;
    private JButton reset;

    // board output
    private JScrollPane jScrollPane1;
    private JLabel status;
    private JTextArea textArea;

    // manual control
    private JButton cwButt;
    private JButton ccwButt;
    private JButton wButt;
    private JButton swButt;
    private JButton seButt;
    private JButton eButt;

    final static Color HILIT_COLOR = Color.LIGHT_GRAY;

    final Highlighter hilit;
    final Highlighter.HighlightPainter painter;

    List<Pair<Board,MoveEncoding>> boards = new ArrayList<>();

    public TextFieldDemo(Board board) {
        initialBoard = board;
        boards.add(Pair.of(board, null));

        initComponents();

        fillText("Let's go!");

        hilit = new DefaultHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter(HILIT_COLOR);
        textArea.setHighlighter(hilit);

        cwButt.addActionListener(e -> makeMove(MoveEncoding.CW));
        wButt.addActionListener(e -> makeMove(MoveEncoding.W));
        swButt.addActionListener(e -> makeMove(MoveEncoding.SW));
        seButt.addActionListener(e -> makeMove(MoveEncoding.SE));
        eButt.addActionListener(e -> makeMove(MoveEncoding.E));
        ccwButt.addActionListener(e -> makeMove(MoveEncoding.CCW));

        execute.addActionListener(e -> playSequence());
        reset.addActionListener(e -> resetBoard());
    }

    private void resetBoard() {
        boards.clear();
        boards.add(Pair.of(initialBoard, null));
        fillText("Reset");
    }

    private void playSequence() {
        String sequence = entry.getText().toLowerCase();
        System.out.println("Playing " + sequence);
        for(int i=0;i<sequence.length();i++) {
            Character c = sequence.charAt(i);
            if (!wordsToMoves.containsKey(c) && !ignored.contains(c)) {
                fillText("Invalid character " + c + " in sequence.");
                return;
            }
        }
        System.out.println("Valid moves in phrase.");

        int i;
        for(i = 0;i<sequence.length();i++) {
            MoveEncoding e = wordsToMoves.get(sequence.charAt(i));
            if (e==null) continue;
            if (!makeMove(e)) {
                break;
            }
        }
        if (i<sequence.length()) {
            entry.setText(sequence.substring(i));
        }
    }

    private Board getCurrentBoard() {
        return boards.get(boards.size()-1).getLeft();
    }

    private MoveEncoding getCurrentReverseMove() {
        MoveEncoding me = boards.get(boards.size()-1).getRight();
        if (me==null) return null;
        return reverse.get(me);
    }

    private void fillText(String extras) {
        Board b = getCurrentBoard();
        String text = printBoard(b);
        textArea.setText(text + "\n\n" + extras);
    }

    private boolean makeMove(MoveEncoding m) {
        Board b = getCurrentBoard();
        if (b.isFinished()) return true;
        // validate return moves
        if (m==getCurrentReverseMove()) {
            fillText("Invalid move (will return to previous pos).");
            return false;
        }
        // validate allowed rotations
        if (m == MoveEncoding.CW) {
            int nextAngle = (b.getSourceAngle()+1)%6;
            if (!b.getTemplate().getAllowedAngles().contains(nextAngle)) {
                fillText("Invalid move (duplicate on rotation).");
                return false;
            }
        } else if (m == MoveEncoding.CCW) {
            int nextAngle = (b.getSourceAngle()+5)%6;
            if (!b.getTemplate().getAllowedAngles().contains(nextAngle)) {
                fillText("Invalid move (duplicate on rotation).");
                return false;
            }
        }
        // all ok create new board
        Board newB = b.makeMove(m);
        boards.add(Pair.of(newB, m));
        if (newB.getTemplate()!=b.getTemplate()) {
            Pair<Board,Integer> clean = newB.clearLines();
            boards.add(Pair.of(clean.getLeft(), null));
        }
        if (newB.isFinished()) {
            fillText("Game finished.");
        } else {
            fillText("");
        }
        return true;
    }
    /**
     * This method is called from within the constructor to
     * initialize the form.
     */

    private void initComponents() {
        prompt = new JLabel("Seq:");
        entry = new JTextField();
        execute = new JButton("Run");
        reset = new JButton("Reset");

        textArea = new JTextArea();
        status = new JLabel();

        cwButt = new JButton("CW");
        wButt = new JButton("W");
        swButt = new JButton("SW");
        seButt = new JButton("SE");
        eButt = new JButton("E");
        ccwButt = new JButton("CCW");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("TextFieldDemo");

        textArea.setColumns(20);
        textArea.setLineWrap(true);
        textArea.setRows(5);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("Courier", Font.BOLD, 13));
        jScrollPane1 = new JScrollPane(textArea);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        //Create a parallel group for the horizontal axis
        ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

        //Create a sequential and a parallel groups
        SequentialGroup h1 = layout.createSequentialGroup();
        ParallelGroup h2 = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);

        //Add a container gap to the sequential group h1
        h1.addContainerGap();

        //Add a scroll pane and a label to the parallel group h2
        h2.addComponent(jScrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE);
        h2.addComponent(status, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE);

        //Create a sequential group h3
        SequentialGroup h3 = layout.createSequentialGroup();
        h3.addComponent(cwButt);
        h3.addComponent(wButt);
        h3.addComponent(swButt);
        h3.addComponent(seButt);
        h3.addComponent(eButt);
        h3.addComponent(ccwButt);
        //h3.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);

        h3.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);

        SequentialGroup h4 = layout.createSequentialGroup();
        h4.addComponent(prompt);
        h4.addComponent(entry, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE);
        h4.addComponent(execute);
        h4.addComponent(reset);
        h4.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);


        //Add the group h3 to the group h2
        h2.addGroup(h3);
        h2.addGroup(h4);
        //Add the group h2 to the group h1
        h1.addGroup(h2);

        h1.addContainerGap();

        //Add the group h1 to the hGroup
        hGroup.addGroup(GroupLayout.Alignment.TRAILING, h1);
        //Create the horizontal group
        layout.setHorizontalGroup(hGroup);


        //Create a parallel group for the vertical axis
        ParallelGroup vGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        //Create a sequential group v1
        SequentialGroup v1 = layout.createSequentialGroup();
        //Add a container gap to the sequential group v1
        v1.addContainerGap();
        //Create a parallel group v2
        ParallelGroup v2 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
        v2.addComponent(cwButt);
        v2.addComponent(wButt);
        v2.addComponent(swButt);
        v2.addComponent(seButt);
        v2.addComponent(eButt);
        v2.addComponent(ccwButt);
        ParallelGroup v3 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
        v3.addComponent(prompt);
        v3.addComponent(entry);
        v3.addComponent(execute);
        v3.addComponent(reset);
        //Add the group v2 tp the group v1
        v1.addGroup(v2);
        v1.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
        v1.addGroup(v3);
        v1.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
        v1.addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE);
        v1.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
        v1.addComponent(status);
        v1.addContainerGap();

        //Add the group v1 to the group vGroup
        vGroup.addGroup(v1);
        //Create the vertical group
        layout.setVerticalGroup(vGroup);
        pack();
    }

    public static String printBoard(Board board) {
        List<StringBuilder> screen;
        screen = new ArrayList<>();
        for(int y=0;y<board.getHeight();y++) {
            StringBuilder line = new StringBuilder(board.getWidth());
            if (y%2==1) line.append(" ");
            for(int x=0;x<board.getWidth();x++) {
                line.append(". ");
            }
            screen.add(line);
        }

        BitSet bs = board.getCells();
        for(int y=0;y<board.getHeight();y++) {
            for(int x=0;x<board.getWidth();x++) {
                if (bs.get(x+y*board.getWidth())) {
                    screen.get(y).setCharAt(adjX(x,y), '*');
                }
            }
        }

        UnitTemplate template = board.getTemplate();
        if (template!=null) {
            Set<Point> points = template.pointsAtLocation(board.getSourceAngle(), board.getSourceX(), board.getSourceY());
            points.forEach(p -> screen.get(p.y).setCharAt(adjX(p.x, p.y), 'X'));
        }

        StringBuilder field = new StringBuilder();
        for (StringBuilder stringBuilder : screen) {
            field.append(stringBuilder).append("\n");
        }

        return field.toString();
    }

    private static int adjX(int x, int y) {
        return x*2 + (y%2);
    }

    public static void main(String args[]) {

        Parser parser = new Parser();
        Problem p = parser.parse("/problems/problem_0.json");
        Board board = p.createBoards().get(0);
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(() -> {
            //Turn off metal's use of bold fonts
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            new TextFieldDemo(board).setVisible(true);
        });
    }


}
