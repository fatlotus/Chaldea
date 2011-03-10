package chaldea.target;

import chaldea.CompilerTarget;

public abstract class CompatCompilerTarget implements CompilerTarget {
	public void emitAddInstruction(int target, int a, int b) {
		emitCallInstruction(target, "call", a, new int[] { b });
	}
	
	public void emitTailCallInstruction(String method, int callee, int[] arguments) {
		emitCallInstruction(0, method, callee, arguments);
		emitReturn(0);
	}
}