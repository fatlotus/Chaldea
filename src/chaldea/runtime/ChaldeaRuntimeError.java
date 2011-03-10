package chaldea.runtime;

public class ChaldeaRuntimeError extends RuntimeException {
	public ChaldeaRuntimeError(String message) {
		super(message);
	}
	
	public ChaldeaRuntimeError() { }
}