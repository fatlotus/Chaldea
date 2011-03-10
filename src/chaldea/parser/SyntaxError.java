package chaldea.parser;

public class SyntaxError extends RuntimeException {
	public SyntaxError(String message, String context, int lineNumber) {
		super(context + ": line " + lineNumber + ", " + message);
	}
	
	public String toString() {
		return getMessage();
	}
}