package chaldea.runtime;

public class ReturnException extends RuntimeException {
	private ChaldeaValue value;
	
	public ReturnException(ChaldeaValue returnValue) {
		super("(return: " + returnValue + ")");
		
		value = returnValue;
	}
	
	public ChaldeaValue getReturnValue() {
		return value;
	}
}