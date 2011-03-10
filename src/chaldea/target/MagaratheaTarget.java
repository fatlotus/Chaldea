package chaldea.target;

import chaldea.*;

public class MagaratheaTarget extends CompatCompilerTarget {
	@Override
	public void emitFunctionEnter(String container, String identifier, int arguments, int registers) {
		System.out.println("!" + container + identifier);
	}
	
	@Override
	public void emitConstantLoadInstruction(int target, int value) {
		System.out.println("\t#" + (10000 + target) + "\tmem.ptr");
		System.out.println("\t#" + value + "\tmem.write");
	}
	
	@Override
	public void emitAddInstruction(int target, int a, int b) {
		System.out.println("\t#" + (10000 + a) + "\tmem.read");
			// ...
		System.out.println("\tmem.value\talu.operand");
		System.out.println("\t#" + (10000 + b) + "\tmem.read");
		System.out.println("\t#" + (10000 + target) + "\tmem.ptr");
		System.out.println("\tmem.value\talu.op.add");
		System.out.println("\talu.result\tmem.write");
	}
	
	@Override
	public void emitCopyInstruction(int target, int source) {
		System.out.println("\t#" + (10000 + source) + "\tmem.read");
			// ...
		System.out.println("\t#" + (10000 + target) + "\tmem.ptr");
		System.out.println("\tmem.value\tmem.write");
	}
	
	@Override
	public void emitCallInstruction(int returnValue, String methodName, int target, int[] arguments) {
		System.out.println("\tCALL " + methodName);
	}
	
	@Override
	public void emitReturn(int register) {
		System.out.println("\tRETURN");
	}
	
	@Override
	public void emitFunctionExit() { }
	
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
}