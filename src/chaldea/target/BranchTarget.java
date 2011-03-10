package chaldea.target;

import chaldea.*;

public class BranchTarget implements CompilerTarget {
	private CompilerTarget a, b;
	
	public BranchTarget(CompilerTarget aTarget, CompilerTarget bTarget) {
		a = aTarget;
		b = bTarget;
	}
	
	@Override
	public void emitFunctionEnter(String container, String identifier, int arguments, int registers) {
		a.emitFunctionEnter(container, identifier, arguments, registers);
		b.emitFunctionEnter(container, identifier, arguments, registers);
	}
	
	@Override
	public void emitConstantLoadInstruction(int target, int value) {
		a.emitConstantLoadInstruction(target, value);
		b.emitConstantLoadInstruction(target, value);
	}
	
	@Override
	public void emitAddInstruction(int target, int opA, int opB) {
		a.emitAddInstruction(target, opA, opB);
		b.emitAddInstruction(target, opA, opB);
	}
	
	@Override
	public void emitCallInstruction(int returnValue, String methodName, int target, int[] arguments) {
		a.emitCallInstruction(returnValue, methodName, target, arguments);
		b.emitCallInstruction(returnValue, methodName, target, arguments);
	}
	
	@Override
	public void emitCopyInstruction(int target, int source) {
		a.emitCopyInstruction(target, source);
		b.emitCopyInstruction(target, source);
	}
	
	@Override
	public void emitFunctionExit() {
		a.emitFunctionExit();
		b.emitFunctionExit();
	}
	
	@Override
	public void emitReturn(int register) {
		a.emitReturn(register);
		b.emitReturn(register);
	}
	
	@Override
	public void emitLoadConstantClass(int target, String name) {
		a.emitLoadConstantClass(target, name);
		b.emitLoadConstantClass(target, name);
	}
	
	@Override
	public void emitLoadConstantString(int target, String value) {
		a.emitLoadConstantString(target, value);
		b.emitLoadConstantString(target, value);
	}
	
	@Override
	public void emitLoadClosure(int register, int number) {
		a.emitLoadClosure(register, number);
		b.emitLoadClosure(register, number);
	}
	
	@Override
	public void emitLoadNull(int register) {
		a.emitLoadNull(register);
		b.emitLoadNull(register);
	}
	
	@Override
	public void emitSourceLine(String sourceFile, int lineNumber) {
		a.emitSourceLine(sourceFile, lineNumber);
		b.emitSourceLine(sourceFile, lineNumber);
	}
	
	@Override
	public void emitStateVariable(int register, int subject, String slotNumber) {
		a.emitStateVariable(register, subject, slotNumber);
		b.emitStateVariable(register, subject, slotNumber);
	}
	
	@Override
	public void emitWriteStateVariable(int subject, String slotNumber, int source) {
		a.emitWriteStateVariable(subject, slotNumber, source);
		b.emitWriteStateVariable(subject, slotNumber, source);
	}
	
	@Override
	public void emitTailCallInstruction(String method, int callee, int[] arguments) {
		a.emitTailCallInstruction(method, callee, arguments);
		b.emitTailCallInstruction(method, callee, arguments);
	}
}