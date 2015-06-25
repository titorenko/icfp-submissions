package frontend;

/**
 *                interface Guess {
     id: string;
     program: string;
    }

 */
public class Guess implements ProblemRequest {
    private String id;
    private String program;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public static Guess programGuess(String id, String program) {
        Guess guess = new Guess();
        guess.setId(id);
        guess.setProgram(program);
        return guess;
    }

    @Override
    public String toString() {
        return "Guess{" +
                "id='" + id + '\'' +
                ", program='" + program + '\'' +
                '}';
    }
}
