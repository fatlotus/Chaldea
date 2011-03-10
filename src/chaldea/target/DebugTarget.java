package chaldea.target;

import chaldea.*;

public class DebugTarget implements CompilerTarget {
	@Override
	public void emitFunctionEnter(String container, String identifier, int numArguments, int numRegisters) {
		System.out.println("func \"" + container + "\" \"" + identifier + "\" (arguments=" + numArguments + ", registers=" + numRegisters + ")");
	}
	
	@Override
	public void emitFunctionExit() {
		System.out.println("exit");
	}
	
	@Override
	public void emitConstantLoadInstruction(int target, int value) {
		System.out.println("\tLOADI\tR" + target + " #" + value);
	}
	
	@Override
	public void emitAddInstruction(int target, int a, int b) {
		System.out.println("\tADD\tR" + target + " R" + a + " R" + b);
	}
	
	@Override
	public void emitCopyInstruction(int target, int source) {
		System.out.println("\tCOPY\tR" + target + " R" + source);
	}
	
	@Override
	public void emitCallInstruction(int returnValue, String methodName, int target, int[] arguments) {
		System.out.print("\tCALL\tR" + returnValue + " \"" + methodName + "\" R" + target);
		
		for (int argument : arguments) {
			System.out.print(" R" + argument);
		}
		
		System.out.println();
	}
	
	@Override
	public void emitLoadConstantClass(int target, String name) {
		System.out.println("\tLOADT\tR" + target + " \"" + name +"\"");
	}
	
	@Override
	public void emitLoadConstantString(int target, String value) {
		if (value.length() > 30) {
			value = value.substring(0, 30) + " ...";
		}
		
		System.out.println("\tLOADS\tR" + target + " \"" + value + "\"");
	}	
	
	@Override
	public void emitLoadClosure(int register, int number) {
		System.out.println("\tLOADC\tR" + register + " $" + number);
	}
	
	@Override
	public void emitReturn(int register) {
		System.out.println("\tRETURN R" + register);
	}
	
	@Override
	public void emitLoadNull(int register) {
		System.out.println("\tLOADN\tR" + register);
	}
	
	@Override
	public void emitSourceLine(String sourceFile, int lineNumber) {
		System.out.println("\t\t# " + sourceFile + ":" + lineNumber);
	}
	
	@Override
	public void emitStateVariable(int register, int subject, String variableName) {
		System.out.println("\tLOADV\tR" + register + " R" + subject + "@" + variableName);
	}
	
	@Override
	public void emitWriteStateVariable(int subject, String variableName, int source) {
		System.out.println("\tSTOREV\tR" + subject + "@" + variableName + " R" + source);
	}
	
	@Override
	public void emitTailCallInstruction(String method, int callee, int[] arguments) {
		System.out.println("\tTAILC\t" + method + " R" + callee);
		
		for (int argument : arguments) {
			System.out.print(" R" + argument);
		}
		
		System.out.println();
	}
}