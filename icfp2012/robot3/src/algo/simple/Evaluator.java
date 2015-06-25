package algo.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Mine;
import model.Move;
import model.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import algo.simple.heuristics.Delta;
import algo.simple.heuristics.HeuristicalEngine;
import algo.simple.lookahead.LookaheadEvaluatedMove;
import algo.simple.lookahead.LookaheadEvaluationContext;
import algo.simple.lookahead.LookaheadMoveSequence;

public class Evaluator {
	public static final int TIME_LIMIT_SEC = 150;
    private static final Logger logger = LoggerFactory.getLogger(Evaluator.class);

    private final HeuristicalEngine engine;
    private EvaluationContext ctx;
    LookaheadEvaluationContext lookaheadContext;
    
    private volatile boolean interrupted = false;
    
    private CurrentLine line = new CurrentLine(this);
	private BackJumpEvaluator backJumpEvaluator;
    
    class ScoredPosition {
		final List<LookaheadMoveSequence> bestMoves;
    	int nextMoveToPlay;
    	
    	public ScoredPosition(List<LookaheadMoveSequence> movesToPlay) {
    		this.bestMoves = movesToPlay;
    		this.nextMoveToPlay = 0;  
		}
    	
    	public boolean hasNoMoves() {
    		return bestMoves.size() == 0;
    	}

		public Delta getScore() {
			return bestMoves.get(0).getEndScore();
		}

		public LookaheadEvaluatedMove nextMove() {
			LookaheadEvaluatedMove result = (nextMoveToPlay < bestMoves.size()) ? bestMoves.get(nextMoveToPlay).getFirstMove() : null;
			nextMoveToPlay++;
			return result;
		}
		
		public LookaheadEvaluatedMove currentMove() {
			if (bestMoves.size() == 0) return null;
			int playedMove = Math.max(0, Math.min(nextMoveToPlay - 1,  bestMoves.size() - 1));
			return bestMoves.get(playedMove).getFirstMove();
		}
		
		@Override
		public String toString() {
			return "next="+nextMoveToPlay+" moves="+bestMoves;
		}

		public boolean hasNoNextMove() {
			return nextMoveToPlay >= bestMoves.size();
		}
		
    }

    public Evaluator(Mine mine, HeuristicalEngine engine) {
        this.engine = engine;
    	this.ctx = new EvaluationContext(engine);
    }
    
    public EvaluationContext getCtx() {
		return ctx;
	}

    public Path getPath(Mine mine) {
		init(mine);
		findPath();
		optimize(mine);
        logger.info("getpath finished " + ctx);
		return ctx.getAbsoluteBestPath();
	}

	private void init(Mine mine) {
		ctx = new EvaluationContext(engine);
		reset();
		line.add(mine);
	}

	private void reset() {
		ctx.resetStuckness();
		line = new CurrentLine(this);
		backJumpEvaluator = new BackJumpEvaluator();
	}

	private void optimize(Mine game) { 
		Path path = ctx.getAbsoluteBestPath();
		FinalSolutionOptimizer optimizer = new FinalSolutionOptimizer(game, path);
		Path optimized = optimizer.optimize();
		ctx.record(game.makeMoves(optimized));
	}

	private void findPath() {
		while(!interrupted && !ctx.isStuck()) {
			
			LookaheadEvaluatedMove nextMove = line.nextMove();
			if (nextMove == null) {
				return;
			}
			Mine newMine = line.currentMine().makeMove(nextMove.getMove());
			boolean shouldGoDeeper = shouldGoDeeper(newMine);
			if (shouldGoDeeper) {
				line.add(newMine);
				Action action = backJumpEvaluator.onDepthEncrease(ctx, line);
				if (action == Action.BACKTRACK) {
					line.backTrack();
				} else if (action == Action.BACKJUMP) {
					int length = line.currentPath().length();
					int s = length * backJumpEvaluator.nBackJumps / (backJumpEvaluator.nBackJumps+1) + backJumpEvaluator.nBackJumps;
					int jumpLength = Math.min(s, length);
					while(line.getPosition(length-jumpLength).hasNoNextMove()) {
						jumpLength--;
					}
					Path original = line.currentPath();
					for (int i = 0; i < jumpLength; i++) {
						line.backTrack();
					}
					System.out.println("BACKJUMP of size "+jumpLength+" from\n"+original+" ->\n"+line.currentPath());
				} 
			}
		}
	}


	private boolean shouldGoDeeper(Mine mine) {
		Integer seenOnMove = ctx.record(mine);
		if (seenOnMove != null && seenOnMove <= mine.getRobot().getPath().length()) return false;
		if (mine.getState().isFinished()) return false;
		return true;
	}
		
	List<LookaheadMoveSequence> findBestMovesThrottled(Mine mine) {
		long start = System.currentTimeMillis();
		List<LookaheadMoveSequence> movesToPlay = findBestMoves(mine);
		int evalMovesTime = (int) Math.abs(System.currentTimeMillis() - start);
		ctx.throttle(mine, evalMovesTime);
		//System.out.println("For mine\n "+mine+" best moves: "+movesToPlay);
		return movesToPlay;
	}
	
	public List<LookaheadMoveSequence> findBestMoves(Mine mine) {
		List<LookaheadMoveSequence> movesToPlay = new ArrayList<LookaheadMoveSequence>();
		this.lookaheadContext = new LookaheadEvaluationContext(mine, ctx);
		List<Move> moves = mine.getPossibleRobotMoves();
		for (Move move : moves) {
			evalMove(mine, move, moves.size());
			LookaheadMoveSequence bestPath = lookaheadContext.getBestPath();
			if (bestPath != null) movesToPlay.add(bestPath);
			lookaheadContext.resetBestPath();
		}
		Collections.sort(movesToPlay);
		return movesToPlay;
	}
	
	
	void evalMove(Mine mine, Move move, int nCandidatesConsidered) {
		try {
			Mine newMine = mine.makeMove(move);
			boolean finishEval = lookaheadContext.visitNewMine(newMine, move, nCandidatesConsidered);
			if (finishEval) return;
			
			List<Move> moves = newMine.getPossibleRobotMoves();
			for (Move newMove : moves) {
				evalMove(newMine, newMove, moves.size());
			}
		} finally {
			lookaheadContext.pop();
		}
	}

    public void stopWorking() {
        interrupted = true;
    }

}




