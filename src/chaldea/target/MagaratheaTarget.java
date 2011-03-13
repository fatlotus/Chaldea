package chaldea.target;

import chaldea.*;

public class MagaratheaTarget extends CompatCompilerTarget {
	private final int STACK_PTR_ADDRESS = 0x3000;
	private final int STATIC_ADDRESS = 0x2000;
	private final int SCREEN_PTR = 0x4000;
	
	@Override
	public void emitFunctionEnter(String container, String identifier, int arguments, int registers) {
		System.out.println("!" + container + identifier);
	}
	
	protected void asm(String lhs, String rhs) {
		System.out.println("\t" + lhs + "\t" + rhs);
	}
	
	protected String getRegister(int reg) {
		asm("#" + STATIC_ADDRESS + reg, "mem.addr");
		asm("#0", "mem.read");
		return "mem.result";
		
		/* asm("#" + STACK_PTR, "mem.addr");
		asm("#0", "mem.read");
		asm("mem.result", "alu.operand");
		asm("#" + reg, "alu.add");
		asm("alu.result", "mem.addr");
		asm("#0", "mem.read");
		asm("#0"); */
	}
	
	protected void setRegister(String value, int register) {
		asm("#" + STATIC_ADDRESS + reg, "mem.addr");
		asm(value, "mem.write");
		
		/* asm("#" + STACK_PTR, "mem.addr");
		asm("#0", "mem.read");
		asm("mem.result", "alu.operand");
		asm("#" + register, "alu.add");
		asm("alu.result", "mem.addr");
		asm(value, "mem.write"); */
	}
	
	@Override
	public void emitConstantLoadInstruction(int target, int value) {
		setRegister("#" + value, target);
	}
	
	@Override
	public void emitAddInstruction(int target, int a, int b) {
		
	}
	
	@Override
	public void emitCopyInstruction(int target, int source) {
		/* asm("#" + STACK_PTR, "mem.addr");
		asm("#0", "mem.read");
		asm("mem.result", "alu.op");
		asm("#" + source, "alu.add");
		asm("alu.result", "mem.addr");
		asm("#0", "mem.read");
		asm("#" + target, "alu.add");
		asm("alu.result", "mem.addr");
		asm("mem.result", "mem.write");
		*/
		
		setRegister(getRegister(source), target);
	}
	
	@Override
	public void emitCallInstruction(int returnValue, String methodName, int target, int[] arguments) {
		if (methodName.equals("print")) {
			asm("#" + SCREEN_PTR, "mem.read");
			asm("mem.result", "alu.op");
			asm("#" + 0x8000, "alu.add");
			asm("alu.op", "mem.addr");
			asm("", "mem.write");
		}
	}
	
	@Override
	public void emitReturn(int register) { }
	
	@Override
	public void emitFunctionExit() { }
	
	@Override
	public void emitLoadConstantClass(int target, String name) { }
	
	@Override
	public void emitLoadConstantString(int target, String value) {
		
	}
	
	@Override
	public void emitLoadClosure(int target, int closure) { }
	
	@Override
	public void emitLoadNull(int register) {
		setRegister("#0", register);
	}
	
	@Override
	public void emitSourceLine(String sourceFile, int lineNumber) { }
	
	@Override
	public void emitStateVariable(int register, int subject, String slotNumber) { }
	
	@Override
	public void emitWriteStateVariable(int subject, String slotNumber, int source) { }
}