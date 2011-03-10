package chaldea.runtime;

import chaldea.CompilerTarget;

public interface Method {
	public String getName();
	public int getNumberOfRegisters();
	public int getNumberOfArguments();
	public void evaluateInto(CompilerTarget target);
	public ChaldeaValue evaluate(TypeSpace ts, ChaldeaValue callee, ChaldeaValue[] arguments);
	public boolean isNative();
}