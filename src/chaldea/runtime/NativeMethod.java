package chaldea.runtime;

import chaldea.CompilerTarget;

public abstract class NativeMethod implements Method {
	public abstract String getName();
	public abstract int getNumberOfArguments();
	public ChaldeaValue evaluate(TypeSpace ts, ChaldeaValue callee, ChaldeaValue[] arguments) {
		if (getNumberOfArguments() != arguments.length && getNumberOfArguments() != -1) {
			throw new ChaldeaRuntimeError("invalid number of arguments: " +
			  arguments.length + " for " + getNumberOfArguments());
		}
		
		return methodBody(ts, callee, arguments);
	}
	protected abstract ChaldeaValue methodBody(TypeSpace ts, ChaldeaValue callee, ChaldeaValue[] arguments);
	
	public int getNumberOfRegisters() { return 0; }
	
	public void evaluateInto(CompilerTarget target) {
		throw new ChaldeaRuntimeError("native methods cannot be evaluated as VM instructions.");
	}
	
	public boolean isNative() { return true; }
}