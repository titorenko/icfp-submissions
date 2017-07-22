package algo;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import model.Board;
import runner.AnnealingSolverConfig;

public class AnnealingSolver {
    
    private int bestScore = Integer.MIN_VALUE;
    private SearchNode bestSolution = null;
    
    private StepNStructure[] structures;
	private AnnealingSolverConfig cfg;
    
    public AnnealingSolver(Board initial) {
    	this(initial, AnnealingSolverConfig.DEFAULT);
    }
    
    public AnnealingSolver(Board initial, AnnealingSolverConfig cfg) {
    	this.cfg = cfg;
        initStructures(initial);
    }

    private void initStructures(Board initial) {
        this.structures = new StepNStructure[initial.getSourcesRemaining()];
        for (int i = initial.getSourcesRemaining() - 1; i >=0 ; i--) {
            structures[i] = new StepNStructure(i);
        }
        structures[0].insert(SearchNode.initial(initial, cfg), 1);
    }
    
    public SearchNode solve() {
        int nIterations = 0;
        double T = cfg.initialTemp;
        do {
            runIteration(T);
            nIterations++;
            T *= cfg.cooldownCoefficient;
            if (nIterations % cfg.logFreq == 0) System.out.println(nIterations+" t="+T+": "+stats());
        } while (totalSize() > 0);
        return bestSolution;
    }
    
    private void runIteration(double t) {
        for (StepNStructure s : structures) {
            s.runIteration(t);
        }
    }
    
    public int totalSize() {
        int total = 0;
        for (StepNStructure s : structures) {
            total += s.queue.size();
        }
        return total;
    }
    
    public String stats() {
        int totalSize = 0;
        int totalRejected  = 0;
        int totalInferiorAccepted  = 0;
        int totalBetterAccepted  = 0;
        
        for (StepNStructure s : structures) {
            totalSize += s.queue.size();
            totalRejected  += s.nRejected;
            totalInferiorAccepted += s.nInferiorAccepted;
            totalBetterAccepted += s.nBetterAccepted;
        }
        return "totalSize="+totalSize+", totalRejected="+totalRejected+", totalInferiorAccepted="+totalInferiorAccepted
        		+", totalBetterAccepted="+totalBetterAccepted;
    }
    
    Random rnd = new Random();

    private class StepNStructure {
        final int step;
        final MinPQ<SearchNode> queue = new MinPQ<>(100, new QComparator());
        final Set<SearchNode> closed = new HashSet<>();
        final StepNStructure nextStructure;
        SearchNode bestLocalSolution;
        
        int nRejected = 0;
        int nInferiorAccepted = 0;
        int nBetterAccepted = 0;
        
        StepNStructure(int step) {
            this.step = step;
            this.nextStructure = (step < structures.length - 1) ? structures[step+1] : null;
        }
        
        void insert(SearchNode sn, double t) {
            int bestLocal = bestLocalSolution == null ? 0 : getScore(bestLocalSolution);
            int score = getScore(sn);
            recordBestScore(sn);
            if (score > bestLocal) {
                queue.insert(sn);
                nBetterAccepted++;
            } else if (t > 1E-20){
                double r = rnd.nextDouble();
                double pr = Math.exp((score - bestLocal) / t);
                if (r < pr) {
                    queue.insert(sn);
                    nInferiorAccepted++;
                } else {
                	nRejected++;
                }
            }
        }

        void runIteration(double t) {
            if (queue.isEmpty()) return;
            SearchNode sn = queue.delMin();
            boolean contained = !closed.add(sn);
            if (contained) return;
            List<SearchNode> nextNodes = sn.selectNextMovesForAnnealingSolver();
            for (SearchNode nextNode : nextNodes) {
                if (nextStructure != null) nextStructure.insert(nextNode, t);
            }
        }

        private void recordBestScore(SearchNode sn) {
            if (sn.getScore() > bestScore) {
                bestScore = sn.getScore();
                bestSolution = sn;
                ExecutionLog.INSTANCE.onBestNode(bestScore, bestSolution, 0);
            }
            if (bestLocalSolution == null || getScore(sn) > getScore(bestLocalSolution)) {
                bestLocalSolution = sn;
            }
        }
    }
    
    class QComparator implements Comparator<SearchNode> {
        public int compare(SearchNode s1, SearchNode s2) {
            int r = -getScore(s1) + getScore(s2);
            if (r == 0) return -s1.heuristicScore() + s2.heuristicScore();
            return r;
        }
    }
    
    private int getScore(SearchNode s1) {
    	Board b = s1.getBoard();
    	int linesBonus = 0;//Math.min((b.getFillCount()+b.getSourcesRemaining())/b.getWidth(), b.getSourcesRemaining()) * 10;
    	int powerBonus = 0; //b.getSourcesRemaining() * (b.getHeight() - b.getNFilledRows());
    	return s1.getScore() + linesBonus + powerBonus;
    }
} 