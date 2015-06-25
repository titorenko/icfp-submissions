package runner;

import java.io.InputStream;
import java.util.Properties;

import model.ElementsConfig;
import model.Mine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parser.ElementsConfigParser;
import parser.Input;
import parser.LoggerOutput;
import parser.MapParser;
import parser.MapPrinter;
import parser.Output;
import parser.StreamInput;
import sun.misc.Signal;
import sun.misc.SignalHandler;
import algo.Algorithm;

/**
 * Runner!
 * <p/>
 * arg0 - algo, arg1 - map (optional)
 * if no arguments are given, algo is taken from submission.properties and map is read from stdin
 */
public class BotRunner {

    private static final Logger logger = LoggerFactory.getLogger(BotRunner.class);
    private final Input input;
    private final Output output;
    private final String algorithmName;
    private Algorithm algo;

    public BotRunner(Input input, Output output, String algorithmName) {

        this.input = input;
        this.output = output;
        this.algorithmName = algorithmName;
    }

    public void init() {
        try {
            Class<?> algoClass = Class.forName(algorithmName);
            algo = (Algorithm) algoClass.newInstance();

            Signal.handle(new Signal("INT"), new SignalHandler() {
                public void handle(Signal sig) {
                    algo.stopWorking();
                }
            });
        } catch (ClassNotFoundException e) {
            logger.error("Failed to create algorithm class " + algorithmName, e);
            throw new RuntimeException("Failed to create algorithm class " + algorithmName, e);
        } catch (InstantiationException e) {
            logger.error("Failed to create algorithm class " + algorithmName, e);
            throw new RuntimeException("Failed to create algorithm class " + algorithmName, e);
        } catch (IllegalAccessException e) {
            logger.error("Failed to create algorithm class " + algorithmName, e);
            throw new RuntimeException("Failed to create algorithm class " + algorithmName, e);
        }
    }

    public void run() {
        MapParser.MineInfo initialMine = new MapParser(input).readMap();
        ElementsConfig config = new ElementsConfigParser(input).parseConfig();
        String result = algo.getPath(new Mine(initialMine.width, config, initialMine.cells));
        Mine terminal = algo.getTerminalMine();
        if (terminal!=null) {
            logger.info("Final map:");
            new MapPrinter(output).printMap(terminal);
        }
        // this is intended! :-)
        System.out.println(result);
    }


    public static void main(String[] args) {
        try {
            String algoClass;
            if (args.length == 0) {
                InputStream input = BotRunner.class.getResourceAsStream("/submission.properties");
                if (input==null) {
                    logger.error("Failed to load properties file from jar and no algo is specified.");
                    System.exit(-1);
                }
                Properties props = new Properties();
                props.load(input);
                algoClass = props.getProperty("algo.class");
            } else {
                algoClass = args[0];
            }
            if (args.length > 2) {
                logger.error("Too many arguments");
                System.exit(-1);
            }

            Input input;
            if (args.length == 2) {
                input = new StreamInput(args[1]);
            } else {
                input = new StreamInput(System.in);
            }

            BotRunner runner = new BotRunner(input, new LoggerOutput("DebugFinalState"), algoClass);
            runner.init();
            runner.run();

        } catch (Exception e) {
            logger.error("Failed to execute bot.", e);
            System.exit(-1);
        }
    }
}
