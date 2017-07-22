package algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import model.Board;
import model.MoveEncoding;
import model.Score;
import model.UnitTemplate;
import powerwords.PowerMove;
import powerwords.PowerWordExplorer;
import powerwords.Powerword;
import powerwords.Powerwords;
import runner.SolverConfig;

public class SearchNode {
	
	public static SolverConfig config;
	
	private Board board;
	public SearchNode prev;
	private MoveEncoding lastMove;
	private Score score;
	private int[] powerwordsSoFar;
	public SearchNode nextWhenLocked = null;
	private Integer lastCleared = -1;

	private SearchNode(Board board, SolverConfig config) {
		this.board = board;
		this.lastMove = null;
		this.prev = null;
		this.powerwordsSoFar = new int[Powerwords.INSTANCE.size()]; 
		this.score = Score.initial();
		SearchNode.config = config;
	}
	
	public SearchNode(Board board, MoveEncoding lastMove, SearchNode prev) {
		this.board = board;
		this.lastMove = lastMove;
		this.prev = prev;
		this.powerwordsSoFar = Arrays.copyOf(prev.powerwordsSoFar, prev.powerwordsSoFar.length);

		Pair<Board, Integer> cleared = getClearedLines(prev, this);
		this.lastCleared = cleared.getRight();
		if (lastCleared < 0) {
			this.score = prev.score.cloneScore();
		} else {
			this.score = prev.score.addMove(prev.board.getTemplate().getSize(), lastCleared);
			this.board = cleared.getLeft();
		}
	}
	
	public boolean isLastMovePlacement() {
		return lastCleared >= 0;
	}
	
	public void onPowerWord(Powerword powerword) {
		int count = powerwordsSoFar[powerword.ord];
	    int deltaSpellScore = Powerwords.deltaScoreOf(score.getSpellScore(), count, powerword);
	    this.score.setSpellScore(deltaSpellScore);
	    
	    powerwordsSoFar[powerword.ord] = count + 1;
	    assert deltaSpellScore == Powerwords.scoreOf(powerwordsSoFar);
    }
	
	public int getYSum() {
		UnitTemplate t = getBoard().getTemplate();
		return t == null ? 0 : t.getYSum(getBoard());
	}
	
	private int getNeighbourFactor() {
		UnitTemplate t = getBoard().getTemplate();
		return t == null ? 0 : t.getFilledNeighbourCount(board);
	}

	int getPlacementRowsFill() {
	    UnitTemplate t = getBoard().getTemplate();
        return t == null ? 0 : t.getPlacementRowsFill(board);
    }
	
	public List<SearchNode> neighboursNoPlacement() {
	    List<SearchNode> result = new ArrayList<>(6);
	    Iterable<Pair<Board, MoveEncoding>> neighbors = board.neighbors();
        for (Pair<Board, MoveEncoding> nb : neighbors) {
            if (nb.getLeft().getSourceIndex() == getBoard().getSourceIndex()) {
                result.add(next(nb));
            } else {
                this.nextWhenLocked = next(nb);
            }
        }
        return result;
    }
	
	public MoveEncoding getLastMove() {
		return lastMove;
	}
	
	private Pair<Board, Integer> getClearedLines(SearchNode prev, SearchNode cur) {
		if (prev.board.getSourceIndex() == cur.board.getSourceIndex()) {
			return Pair.of(cur.board, -1);
		}
		return board.clearLines();
	}
	
	public int[] getPowerwordsSoFar() {
		return powerwordsSoFar;
	}
	
	public void insertNextMoves(MinPQ<SearchNode> q) {
		long start = System.currentTimeMillis();
		List<SearchNode> next = selectNextMoves();
		ExecutionLog.INSTANCE.onLookAheadFinished(System.currentTimeMillis() - start);
		for (SearchNode searchNode : next) {
			q.insert(searchNode);
		}
	}

	private List<SearchNode> selectNextMoves() {
		return lookAheadSelection(config.schedule);
	}
	
	public List<SearchNode> selectNextMovesForAnnealingSolver() {
	    /*BoardExplorer boardExplorer = new BoardExplorer(this);
	    Collection<SearchNode> reachable = boardExplorer.computeReachableFinalNodesWithScores().values();
	    List<SearchNode> result = new ArrayList<>();
	    for (SearchNode searchNode : reachable) {
	        SearchNode optimized = optimizeForPowerwords(searchNode.nextWhenLocked); 
	        result.add(optimized);
        }
        return result;*/
		 return new BellmanFordBoardExplorer(this).computeReachableFinalNodesWithScores()
		            .stream().collect(Collectors.toList());

    }

	private int getFillBonus() {
		if (config.fillFactor == 0) return 0;
		int n = getBoard().getNFilledRows();
        int h = getBoard().getHeight();
        double f = 1.0 - (double)n/(double)h;
        if (f < 0.25) f = 0.0;
        
        int fillBonus = (int) (getBoard().getFillCount() * f);
		return config.fillFactor * fillBonus;
	}
	
	private List<SearchNode> lookAheadSelection(List<Integer> branchingFactors) {
		BoardExplorer boardExplorer = new BoardExplorer(this);
		Map<Integer, SearchNode> reachable = boardExplorer.computeReachableFinalNodesWithScores();
	    List<SearchNode> nodes = selectNodes(reachable.values(), branchingFactors.get(0));
	    List<Pair<SearchNode, SearchNode>> leve1WithBestLookahead = new ArrayList<>();
	    for (SearchNode level1Node : nodes) {
	    	SearchNode levelBest = lookAheadSelection(Collections.singletonList(level1Node.nextWhenLocked), 
	    			branchingFactors.subList(1, branchingFactors.size()));
	    	if (levelBest == null) continue;
	    	leve1WithBestLookahead.add(Pair.of(level1Node, levelBest));
		}
	    if (leve1WithBestLookahead.size() == 0) return Collections.emptyList();
	    Collections.shuffle(leve1WithBestLookahead, config.rnd);
	    Collections.sort(leve1WithBestLookahead, new Comparator<Pair<SearchNode, SearchNode>>() {
			@Override
			public int compare(Pair<SearchNode, SearchNode> p1, Pair<SearchNode, SearchNode> p2) {
				int r = -p1.getRight().getLookaheadHeuristic() + p2.getRight().getLookaheadHeuristic();
				if (r==0) return -p1.getLeft().heuristicScore() + p2.getLeft().heuristicScore();
				return r;
			}
		});
	    
	    List<SearchNode> result = new ArrayList<>();
	    result.add(leve1WithBestLookahead.get(0).getLeft().nextWhenLocked);
	    
	   /* if (config.rnd.nextInt(35) == 0 && leve1WithBestLookahead.size() > 1) {
	    	result.add(leve1WithBestLookahead.get(1).getLeft().nextWhenLocked);
	    }
	    if (config.rnd.nextInt(200) == 0 && leve1WithBestLookahead.size() > 2) {
	    	int idx = 2 + config.rnd.nextInt(leve1WithBestLookahead.size() - 2);
	    	result.add(leve1WithBestLookahead.get(idx).getLeft().nextWhenLocked);
	    }*/
	    
	    return result
	    	.stream()
	    	.map(this::optimizeForPowerwords)
	    	.collect(Collectors.toList());
	    
	  /*  return leve1WithBestLookahead
	    	.subList(0, 1)
	    	.stream()
	    	.map(p -> p.getLeft().nextWhenLocked)
	    	.map(this::optimizeForPowerwords)
	    	.collect(Collectors.toList());*/
	}
	
	private SearchNode optimizeForPowerwords(SearchNode best) {
		if (best == null)
            return null;
        PowerWordExplorer pwex = new PowerWordExplorer(this, best.prev);
        PowerMove pw = pwex.findPowerWordPathFromTo();
        if (pw != null && pw.getToNode().getPowerScore() > best.prev.getPowerScore()) {
        	return pw.getToNode();
        }
        return best;
	}


	private SearchNode lookAheadSelection(List<SearchNode> toExplore, List<Integer> branchingFactors) {
		if (branchingFactors.size() == 0) {
			return selectBestFromLookahead(toExplore);
		}
		List<SearchNode> nextLevel = new ArrayList<>();
		for (SearchNode n : toExplore) {
			BoardExplorer boardExplorer = new BoardExplorer(n);
			Map<Integer, SearchNode> reachable = boardExplorer.computeReachableFinalNodesWithScores();
			List<SearchNode> nodes = selectNodes(reachable.values(), branchingFactors.get(0));
			nextLevel.addAll(nodes.stream().map(nn -> nn.nextWhenLocked).collect(Collectors.toList()));
		}
		if (nextLevel.size() == 0) {
			return selectBestFromLookahead(toExplore);
		}
		return lookAheadSelection(nextLevel, branchingFactors.subList(1, branchingFactors.size()));
	}

	private SearchNode selectBestFromLookahead(List<SearchNode> toExplore) {
		SearchNode bestNode = null;
		int bestScore = Integer.MIN_VALUE;
		for (SearchNode n4 : toExplore) {
			if (n4.getLookaheadHeuristic() > bestScore) {
				bestScore = n4.getLookaheadHeuristic();
				bestNode = n4;
			}
		}
		return bestNode;
	}
	
	private List<SearchNode> selectNodes(Collection<SearchNode> reachable, int count) {
		List<SearchNode> representatives = new ArrayList<>(reachable);
		Collections.shuffle(representatives, config.rnd);
		if (count == -1) return representatives;
		int countNeeded = Math.min(count, representatives.size());
		return selectBestN(representatives, countNeeded);
	}

	private List<SearchNode> selectBestN(List<SearchNode> representatives, int countNeeded) {
		if (countNeeded == 1)
			return Collections.singletonList(selectSingleBest(representatives));
		representatives.sort(Comparator.comparing(SearchNode::heuristicScore).reversed());
	    return representatives.subList(0, Math.min(countNeeded, representatives.size()));
	}

	private SearchNode selectSingleBest(List<SearchNode> representatives) {
		SearchNode best = representatives.get(0);
		int bestScore = best.heuristicScore();
		for (SearchNode searchNode : representatives) {
			if (searchNode.heuristicScore() > bestScore) {
				best = searchNode;
				bestScore = best.heuristicScore();
			}
		}
		return best;
	}

	public Board getBoard() {
		return board;
	}

	public SearchNode next(Pair<Board, MoveEncoding> nb) {
		return new SearchNode(nb.getLeft(), nb.getRight(), this);
	}

	public static SearchNode initial(Board initial, SolverConfig config) {
		return new SearchNode(initial, config);
	}
	
	@Override
    public boolean equals(Object obj) {
        SearchNode other = (SearchNode) obj;
        return board.equals(other.board);
    }
	
	public String getMoves() {
		List<String> stack = new ArrayList<>();
		SearchNode node = this;
		while(node != null) {
			if (node.lastMove != null)
				stack.add(node.lastMove.toString());
			node = node.prev;
		}
		List<String> reversed = Lists.reverse(stack);
		return Joiner.on("").join(reversed);
	}
	
	private List<SearchNode> getNodesInSequence() {
		LinkedList<SearchNode> stack = new LinkedList<>();
		SearchNode node = this;
		while(node != null) {
			stack.addFirst(node);
			node = node.prev;
		}
		return stack;
	}
	
	public String getMoveEncoding() {
		StringBuffer result = new StringBuffer();
		List<SearchNode> nodes = getNodesInSequence();
		for (int i = 1; i < nodes.size(); i++) {
			SearchNode n = nodes.get(i);
			Powerword pw = n.getPowerwordUsed();
			if (pw == null) {
				result.append(nodes.get(i).getLastMove().getEncoding()+"");
			} else {
				result.setLength(result.length() - (pw.size()-1));
				result.append(pw.toString().toLowerCase());
			}
		}
		return result.toString();
	}
	
	private Powerword getPowerwordUsed() {
		int[] pw = getPowerwordsSoFar();
		for (int idx = 0; idx < pw.length; idx++) {
			 int prevValue = prev.powerwordsSoFar[idx];
			 if (prevValue != pw[idx]) {
				 return Powerwords.INSTANCE.ord(idx);
			 }
		}
		return null;
	}
	
	public String getMoveEncodingForSubmission() {
		return getMoveEncoding().replaceAll("'", "\\\\u0027");
	}
	
	public String getSubmissionCurl(int problemId, long seed) {
		return getSubmissionCurl(problemId, seed, "sunNight");
	}

	public String getSubmissionCurl(int problemId, long seed, String tag) {
		return String.format("curl --user :%s -X POST -ssl -H \"Content-Type: application/json\" -d '%s' "
						+ "http://icfp15.de-falco.fr/teams/%s/solutions",
				"da3af8bc-ad82-4e42-93e8-f4c6e7868b86", getSubmissionJson(problemId, seed, tag), "5");
	}

	public String getSubmissionJson(int problemId, long seed) {
		return getSubmissionJson(problemId, seed, "test1");
	}

	public String getSubmissionJson(int problemId, long seed, String tag) {
		return String.format("[{ \"problemId\":%d , \"seed\":%d, \"tag\": \"%s\", \"solution\": \"%s\" }]", problemId, seed, tag, getMoveEncodingForSubmission());
	}

	int computeComponents() {
		return new ConnectedComponents(board).compute();
	}
	
	@Override
	public int hashCode() {
		return board.hashCode();
	}

	private int hScore = Integer.MIN_VALUE;
	
	public int heuristicScore() {
	    if (hScore == Integer.MIN_VALUE) {
	    	hScore = getLookaheadHeuristic()
	    			+ getYSum()
	    			+ getNeighbourFactor()
	    			//+ getPlacementRowsFill()
	    			- getBoard().getFillPenalty(); 
	    }
		return hScore;
	}
	
	private int getLookaheadHeuristic() {
        return getScoreWithoutPower() + getFillBonus();
	}


	public int getScoreWithoutPower() {
		return getScore() - getPowerScore();
	}
	
	public int getScore() {
		return score.getScore();
	}
	
	public int getPowerScore() {
		return score.getSpellScore();
	}

    public boolean canBeLocked() {
        return nextWhenLocked != null;
    }
    
    public int getTrueSourcePosition() {
        return board.getTrueSourcePosition();
    }
    
    @Override
    public String toString() {
    	return "Node score="+getScore()+" power "+getPowerScore()+" used "+powerwordsSoFar+"\n"+board;
    }
}