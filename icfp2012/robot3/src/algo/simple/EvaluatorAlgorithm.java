package algo.simple;

import model.Mine;
import model.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import algo.Algorithm;

public class EvaluatorAlgorithm implements Algorithm {

    private static final Logger logger = LoggerFactory.getLogger(EvaluatorAlgorithm.class);

    private Mine initialGame;
    private Path bestPath;

    private Evaluator evaluator;

    @Override
    public String getPath(Mine game) {
        initialGame = game;
        evaluator = EvaluatorFactory.getDefaultEvaluator(game);
        bestPath = evaluator.getPath(game);
        return bestPath.toString();
    }

    @Override
    public Mine getTerminalMine() {
        return initialGame.makeMoves(bestPath);
    }

    @Override
    public void stopWorking() {
        logger.info("Interrupt received. Notifying algorithm to stop searching.");
        evaluator.stopWorking();
    }
}
