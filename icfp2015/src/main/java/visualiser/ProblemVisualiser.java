package visualiser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.tuple.Pair;

import com.igormaznitsa.jhexed.engine.HexEngine;
import com.igormaznitsa.jhexed.engine.HexEngineModel;
import com.igormaznitsa.jhexed.engine.misc.HexPosition;
import com.igormaznitsa.jhexed.engine.misc.HexRect2D;
import com.igormaznitsa.jhexed.renders.swing.ColorHexRender;

import algo.BoardExplorer;
import algo.SearchNode;
import icfp.io.Parser;
import icfp.io.model.Problem;
import model.Board;
import model.MoveEncoding;
import model.Point;
import model.UnitTemplate;
import runner.SolverConfig;

public class ProblemVisualiser {
	
	private static final String sol = 
			"ia! ia!lei!lblbbbbbbblei!lei!lei!lei!lbbbbbjohn bigbooteddpppppdaalr'lyehr'lyehei!ppppppkyoyodynekpppdpplanet 10kkblllllthe laundrykkkppppkblue hadeskppdddyogsothothdddalaayuggothr'lyehei!pppktsathogguabbbdddlllbddpkpmonkeyboykkpkkppkpnecronomiconpppplei!kbbbvigintilliondpddadlr'lyehr'lyehpppppdei!lkar'lyehr'lyehppppdddllr'lyehr'lyehr'lyehbdbei!ddlr'lyehr'lyehr'lyehdppalllr'lyehr'lyehbei!r'lyehcthulhu fhtagn!dbbbdlblr'lyehr'lyehei!pr'lyehpppkppdddlr'lyehei!kppppkcase nightmare greenpkpkaapaei!r'lyehei!dpppkyuggothar'lyehr'lyehr'lyehkppppppdddpdllr'lyehdppppkkyoyodynedlr'lyehr'lyehei!dddr'lyehlr'lyehei!kkpr'lyehklbar'lyehr'lyehei!pppppaaei!r'lyehei!pppdalr'lyehr'lyehei!ppppplr'lyehei!kei!kbei!kbei!dbbbbkbkaei!r'lyehr'lyehkpallaei!kei!r'lyehr'lyehr'lyehdaei!aei!lei!lei!lbbbllei!r'lyehr'lyehr'lyehkppkei!kbkbar'lyehr'lyehr'lyehppkei!pkkei!aei!ia! ia!ar'lyehr'lyehei!pppkei!dppkar'lyehr'lyehei!dbbbr'lyehbklr'lyehr'lyehr'lyehbddar'lyehr'lyehkppppr'lyehkbbar'lyehr'lyehei!ppkei!ar'lyehei!dddppdddr'lyehddlr'lyehr'lyehddppr'lyehkllablue hadesei!ppppr'lyehlr'lyehr'lyehr'lyehei!kbkbkblr'lyehr'lyehr'lyehppdpalr'lyehr'lyehr'lyehei!dbblr'lyehr'lyehr'lyehppppdei!dppkei!dbddddei!dbdlei!r'lyehr'lyehr'lyehei!kbbaei!r'lyehr'lyehdei!ppppdei!dldaei!kei!r'lyehr'lyehppppkkalr'lyehr'lyehdppddppr'lyehkar'lyehr'lyehdpppppalr'lyehr'lyehr'lyehppkei!dbbkkpkplr'lyehr'lyehr'lyehei!ddddaei!kei!r'lyehr'lyehbkbkalr'lyehr'lyehr'lyehei!pppddppaar'lyehr'lyehei!r'lyehddblr'lyehr'lyehei!dddpr'lyehklr'lyehr'lyehppppkei!dbbkei!daei!r'lyehr'lyehr'lyehblaei!r'lyehr'lyehr'lyehpppdlaei!r'lyehr'lyehr'lyehei!ppalr'lyehr'lyehr'lyehr'lyehbbkar'lyehr'lyehr'lyehei!kpddei!kbkar'lyehr'lyehei!dppppdei!dalei!abbbbbei!ia! ia!llr'lyehr'lyehei!pkei!ppkei!dblar'lyehr'lyehei!dei!dbbar'lyehr'lyehr'lyehei!dei!kbkaei!r'lyehr'lyehr'lyehpppppaar'lyehr'lyehei!kei!dbbablue hadesei!pppppkbei!r'lyehalei!aei!aei!aei!lei!lr'lyehr'lyehr'lyehei!ppppppdei!daei!r'lyehr'lyehr'lyehpkpr'lyehlr'lyehr'lyehr'lyehei!kbbblr'lyehr'lyehr'lyehr'lyehpppppkkkkpablue hadesei!dr'lyehlr'lyehr'lyehr'lyehei!dei!kbaei!r'lyehr'lyehei!pppppdei!lr'lyehr'lyehei!pkei!dbllr'lyehei!kei!kbkar'lyehei!kbbbdpr'lyehkblr'lyehei!dpkpr'lyehlei!pkei!r'lyehdpppr'lyehkllllei!r'lyehr'lyehr'lyehei!pkppppdar'lyehr'lyehr'lyehpppdei!kkkklkpppar'lyehr'lyehr'lyehkkkpr'lyehar'lyehr'lyehppddr'lyehpkpar'lyehr'lyehei!pppppkei!ddar'lyehr'lyehbddei!dbbklei!r'lyehr'lyehkkkkpr'lyehlr'lyehr'lyehr'lyehddpppdei!kbblalr'lyehr'lyehr'lyehei!dei!kbbddar'lyehr'lyehei!ddr'lyehlr'lyehr'lyehppddei!dei!kbar'lyehr'lyehei!ppppppr'lyehpkei!ar'lyehr'lyehppddpr'lyehka";
	private SearchNode node;
	private HexEngine<Graphics2D> engine;
	private JFrame frame;
	private JComponent hexComponent;
	private Map<Integer, SearchNode> reachable = new HashMap<>();
	private List<MoveEncoding> moveQueue;
	private int moveIndex=0;
	private JLabel infoLabel;
	private SearchNode initial;

	private void run() {
		this.frame = new JFrame("JHexed");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		this.engine = new HexEngine<Graphics2D>(48, 48, HexEngine.ORIENTATION_VERTICAL);

		final Color[] ALLOWEDCOLORS = new Color[] { Color.white, Color.orange, Color.blue, Color.red, Color.green,
				Color.magenta, Color.yellow, Color.GRAY };

		engine.setRenderer(new ColorHexRender() {
			@Override @SuppressWarnings("unchecked")
			public Color getFillColor(HexEngineModel<?> model, int col, int row) {
				Object v = model.getValueAt(col, row);
				if (!(v instanceof Pair)) return ALLOWEDCOLORS[0];
				Pair<String, Integer> value = (Pair<String, Integer>) v;
				return ALLOWEDCOLORS[value.getRight() % ALLOWEDCOLORS.length];
			}
			
			public void drawExtra(final HexEngine<Graphics2D> engine, final Graphics2D g, final int col, final int row, final Color borderColor, final Color fillColor) {
				Object v = engine.getModel().getValueAt(col, row);
				if (!(v instanceof Pair)) return;
				Pair<String, Integer> value = (Pair<String, Integer>) v;
			    final Color drawColor;

			    if (fillColor == null) {
			      drawColor = Color.gray;
			    }
			    else {
			      drawColor = new Color((fillColor.getRGB() ^ 0xFFFFFF) & 0x00FFFFFF);
			    }

			    final Rectangle rect = getHexPath().getBounds();
			    final int centerX = rect.width / 2;
			    final int centerY = rect.height / 2;

			    rect.grow(-rect.height / 4, -rect.width / 4);
			    //g.setFont(scaleFont(value.getLeft(), rect, g, textFont));
			    g.setColor(drawColor);
			    g.drawString(value.getLeft(), centerX - rect.width / 2, centerY + g.getFontMetrics().getAscent() / 2);
			  }
			
		});
		
		frame.add(getProblemSelector(), BorderLayout.NORTH);
		this.hexComponent = getHexComponent(engine);
		frame.add(hexComponent, BorderLayout.CENTER);
		loadBoard("problem_0.json");
		frame.pack();
		frame.setVisible(true);
	}
	
	private JComponent getProblemSelector() {
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new GridLayout(2, 1));
		
		String[] names = new File("src/main/resources/problems").list();
		JComboBox<String> pList = new JComboBox<>(names);
		pList.addActionListener(e -> loadBoard(pList.getSelectedItem().toString()));
		jPanel.add(pList);
		
		JPanel rowPanel = new JPanel();
		JButton wButt = new JButton("W");
		JButton eButt = new JButton("E");
        JButton swButt = new JButton("SW");
        JButton seButt = new JButton("SE");
        JButton cwButt = new JButton("CW");
        JButton ccwButt = new JButton("CCW");
        
        rowPanel.add(wButt);
        rowPanel.add(eButt);
        rowPanel.add(swButt);
        rowPanel.add(seButt);
        rowPanel.add(cwButt);
        rowPanel.add(ccwButt);
        jPanel.add(rowPanel);
        
        cwButt.addActionListener(e -> makeMove(MoveEncoding.CW));
        wButt.addActionListener(e -> makeMove(MoveEncoding.W));
        swButt.addActionListener(e -> makeMove(MoveEncoding.SW));
        seButt.addActionListener(e -> makeMove(MoveEncoding.SE));
        eButt.addActionListener(e -> makeMove(MoveEncoding.E));
        ccwButt.addActionListener(e -> makeMove(MoveEncoding.CCW));
        
        JPanel row2Panel = new JPanel();
        row2Panel.setLayout(new FlowLayout());
        jPanel.add(row2Panel);
        
        JTextField textField = new JTextField(20);
        textField.setText(sol);
        textField.addActionListener(e -> loadMoves(textField.getText()));
        row2Panel.add(textField);
        loadMoves(textField.getText());
        
        
        JButton next = new JButton("Next");
        next.addActionListener(e -> makeMove(moveQueue.get(moveIndex++)));
        row2Panel.add(next);
        
        JButton next2 = new JButton("Next2");
        next2.addActionListener(e -> makeMoves());
        row2Panel.add(next2);

        JTextField gotoMoveField = new JTextField(5);
        gotoMoveField.addActionListener(e -> gotoMove(Integer.parseInt(gotoMoveField.getText())));
        row2Panel.add(gotoMoveField);
        
        this.infoLabel = new JLabel();
        row2Panel.add(infoLabel);
		
        
        return jPanel;
	}

	private void gotoMove(int nMoves) {
		node = initial;
		moveIndex = 0;
		new Thread(() -> { 
			for (int i = 0; i < nMoves; i++) {
				if (moveIndex >= moveQueue.size()) return;
				makeMove(moveQueue.get(moveIndex++));
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {}
			}
		}).start();
	}

	private void makeMoves() {
		new Thread(() -> { 
			do {
				if (moveIndex >= moveQueue.size()) return;
				makeMove(moveQueue.get(moveIndex++));
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {}
			} while(!node.isLastMovePlacement());
		}).start();
	}

	private void loadMoves(String text) {
		this.moveQueue = new ArrayList<>(); 
		for (int i = 0; i < text.length(); i++) {
			moveQueue.add(MoveEncoding.encodingOf(text.charAt(i)));
		}
		this.moveIndex = 0;
	}

	private void makeMove(MoveEncoding me) {
		if (node == null) return;
		Board newBoard = node.getBoard().makeMove(me);
		node = node.next(Pair.of(newBoard, me));
		refreshState();
	}

	private void refreshState() {
		BoardExplorer boardExplorer = new BoardExplorer(node);
		this.reachable = boardExplorer.computeReachableFinalNodesWithScores();
		SwingUtilities.invokeLater(() -> {
			hexComponent.repaint();
			infoLabel.setText("  Score: "+node.getScore()+" Power: "+node.getPowerScore());
		});
	}

	private void loadBoard(String pName) {
		Parser parser = new Parser();
	    Problem p = parser.parse("/problems/"+pName);
	    Board board = p.createBoards().get(0);
	    displayInitialBoard(board);
	}

	private void displayInitialBoard(Board board) {
		initial = SearchNode.initial(board, SolverConfig.QUICK);
		node = initial;
		engine.setModel(new GameBoardModel());
		refreshState();
		frame.pack();
	}

	private JComponent getHexComponent(final HexEngine<Graphics2D> engine) {
		JComponent jComponent = new JComponent() {
			@Override
			protected void paintComponent(Graphics g) {
				engine.draw((Graphics2D) g);
			}

			@Override
			public Dimension getPreferredSize() {
				final HexRect2D rect = engine.getVisibleSize();
				return new Dimension(rect.getWidthAsInt(), rect.getHeightAsInt());
			}
		};
		return new JScrollPane(jComponent,
		         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}
	

	private class GameBoardModel implements HexEngineModel<Pair<String, Integer>> {

		@Override
		public void attachedToEngine(HexEngine<?> engine) {}

		@Override
		public void detachedFromEngine(HexEngine<?> engine) {}

		@Override
		public int getColumnNumber() {
			return node.getBoard().getWidth();
		}

		@Override
		public int getRowNumber() {
			return node.getBoard().getHeight();
		}

		@Override
		public Pair<String, Integer> getValueAt(int x, int y) {
			int idx = node.getBoard().toIndex(x, y);
			SearchNode r = reachable.get(idx);
			String hs = r != null ? r.heuristicScore()+"" : "";
			return Pair.of(hs, getColor(x, y));
		}

		private int getColor(int x, int y) {
			if (hasUnitAt(x, y)) 
				return 2;
			return node.getBoard().isSet(x, y) ? 1 : 0;
		}

		private boolean hasUnitAt(int x, int y) {
			Board board = node.getBoard();
			UnitTemplate template = board.getTemplate();
			if (template == null) return false;
			Set<Point> points = template.pointsAtLocation(board.getSourceAngle(), board.getSourceX(), board.getSourceY());
			return points.contains(new Point(x, y));
		}

		@Override
		public Pair<String, Integer> getValueAt(HexPosition pos) {
			return getValueAt(pos.getColumn(), pos.getRow());
		}

		@Override
		public void setValueAt(int col, int row, Pair<String, Integer> value) {
			throw new UnsupportedOperationException();
			
		}

		@Override
		public void setValueAt(HexPosition pos, Pair<String, Integer> value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isPositionValid(int col, int row) {
			return true;
		}

		@Override
		public boolean isPositionValid(HexPosition pos) {
			return true;
		}

	}
	
	 
	public static void main(String... args) {
		new ProblemVisualiser().run();
	}

}