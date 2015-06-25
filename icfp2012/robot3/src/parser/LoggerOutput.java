package parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerOutput implements Output {

    private final Logger logger;

    public LoggerOutput(String logger) {
        this.logger = LoggerFactory.getLogger(logger);
    }

    @Override
    public void printLine(String line) {
        logger.info(line);
    }
}
