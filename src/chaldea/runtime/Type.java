package chaldea.runtime;

import chaldea.CompilerTarget;

public interface Type {
	public int getSizeOfInstance();
	public int defineInstanceVariable(String instanceVariableName);
	public int getSizeOfStaticSegment();
	public String getName();
	public void addMethod(Method func);
	public void methodDispatch(String methodName, CompilerTarget t);
	public Method getMethod(String methodName);
	public void writeTo(CompilerTarget t);
	public boolean definesMethod(String methodName);
}