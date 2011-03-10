package chaldea.target;

import chaldea.CompilerTarget;

public class NullTarget implements CompilerTarget {
	@Override
	public void emitFunctionEnter(String container, String identifier, int arguments, int registers) { }
	
	@Override
	public void emitConstantLoadInstruction(int target, int value) { }
	
	@Override
	public void emitAddInstruction(int target, int a, int b) { }
	
	@Override
	public void emitCallInstruction(int returnValue, String methodName, int target, int[] arguments) { }
	
	@Override
	public void emitCopyInstruction(int target, int source) { }
	
	@Override
	public void emitFunctionExit() { }
	
	@Override
	public void emitReturn(int register) { }
	
	@Override
	public void emitLoadConstantClass(int target, String name) { }
	
	@Override
	public void emitLoadConstantString(int target, String value) { }
	
	@Override
	public void emitLoadClosure(int target, int closure) { }
	
	@Override
	public void emitLoadNull(int register) { }
	
	@Override
	public void emitSourceLine(String sourceFile, int lineNumber) { }
	
	@Override
	public void emitStateVariable(int register, int subject, String slotNumber) { }
	
	@Override
	public void emitWriteStateVariable(int subject, String slotNumber, int source) { }
	
	@Override
	public void emitTailCallInstruction(String method, int callee, int[] arguments) { }
}