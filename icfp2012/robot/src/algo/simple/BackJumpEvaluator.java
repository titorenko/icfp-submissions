package algo.simple;

public class BackJumpEvaluator {
	private static final int BACKTRACK_BOUND = 100;//TODO: try different settings
	
	public int nBackTracks = 0;
	public int nBackJumps = 0;
	
	public Action onDepthEncrease(EvaluationContext ctx, CurrentLine line) {
		if (line.isDeadEnd()) {
			nBackTracks++;
			if (nBackTracks > BACKTRACK_BOUND) {
				nBackTracks = 0;
				nBackJumps++;
				return Action.BACKJUMP;
			}
			return Action.BACKTRACK;
		} else {
			if (nBackTracks > 0)  nBackTracks--;
			return Action.NORMAL;
		}
	}

}

enum Action {
	NORMAL, BACKTRACK, BACKJUMP, OPTIMIZE;
}
