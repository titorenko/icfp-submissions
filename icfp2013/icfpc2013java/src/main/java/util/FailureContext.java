package util;

import frontend.Problem;
import frontend.TrainingProblem;

import java.math.BigInteger;

import static util.Util.prepareData;

public class FailureContext {

    private String solution;
    private Problem problem;
    private TrainingProblem trainingProblem;
    private BigInteger[] inputs;
    private BigInteger[] outputs;

    public static FailureContext realProblem(String solution, Problem problem, String[] inputs, String[] outputs) {
        return new FailureContext(solution, null, problem, prepareData(inputs), prepareData(outputs));
    }

    public static FailureContext trainingProblem(String solution, TrainingProblem trainingProblem, String[] inputs, String[] outputs) {
        return new FailureContext(solution, trainingProblem, null, prepareData(inputs), prepareData(outputs));
    }

    // for json parsing only
    public FailureContext() {
    }

    private FailureContext(String solution, TrainingProblem trainingProblem, Problem problem, BigInteger[] inputs, BigInteger[] outputs) {
        this.trainingProblem = trainingProblem;
        this.problem = problem;
        this.inputs = inputs;
        this.outputs = outputs;
        this.solution = solution;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public TrainingProblem getTrainingProblem() {
        return trainingProblem;
    }

    public void setTrainingProblem(TrainingProblem trainingProblem) {
        this.trainingProblem = trainingProblem;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public BigInteger[] getInputs() {
        return inputs;
    }

    public void setInputs(BigInteger[] inputs) {
        this.inputs = inputs;
    }

    public BigInteger[] getOutputs() {
        return outputs;
    }

    public void setOutputs(BigInteger[] outputs) {
        this.outputs = outputs;
    }
}
