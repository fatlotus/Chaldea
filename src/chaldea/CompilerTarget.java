package chaldea;

public interface CompilerTarget {
	public void emitFunctionEnter(String container, String identifier, int arguments, int registers);
	public void emitConstantLoadInstruction(int target, int value);
	public void emitCallInstruction(int returnValue, String methodName, int target, int[] arguments);
	public void emitCopyInstruction(int target, int source);
	public void emitFunctionExit();
	public void emitReturn(int register);
	public void emitLoadConstantClass(int target, String name);
	public void emitLoadConstantString(int target, String value);
	public void emitLoadClosure(int register, int number);
	public void emitLoadNull(int register);
	public void emitSourceLine(String sourceFile, int lineNumber);
	public void emitStateVariable(int register, int subject, String variableName);
	public void emitWriteStateVariable(int subject, String variableName, int source);
	public void emitAddInstruction(int target, int a, int b);
	public void emitTailCallInstruction(String method, int callee, int[] arguments);
}