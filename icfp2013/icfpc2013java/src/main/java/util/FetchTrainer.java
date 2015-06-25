package util;

import frontend.Frontend;
import frontend.TrainRequest;
import frontend.TrainingProblem;

/**
 *
 */
public class FetchTrainer {
    public static void main(String[] args) {
        Frontend frontend = new Frontend();
        TrainRequest tr = new TrainRequest();
        tr.setSize(42);
        TrainingProblem tp = frontend.train(tr);
        System.out.println(tp);
    }
}
