package submission;

public class SubmissionFailedException extends Exception {

	private String problemId;

	public SubmissionFailedException(String message, String problemId) {
		super(message);
		this.problemId = problemId;
	}
	
	public String getProblemId() {
		return problemId;
	}

}
