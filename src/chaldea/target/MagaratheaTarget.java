package chaldea.target;

import chaldea.CompilerTarget;
import magarathea.Runner;
import magarathea.Assembler;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class MagaratheaTarget extends CompatCompilerTarget {
	private final int STACK_PTR_ADDRESS = 0x3000;
	private final int STATIC_ADDRESS = 0x2000;
	private final int SCREEN_PTR = 0x4000;

	interface Assemblee {
		int getSize();
		void assemble(Assembler asm, ByteArrayOutputStream outputStream);
	}
	
	class TupleSegment implements Assemblee {
		String a;
		String b;
		
		TupleSegment(String x, String y) {
			a = x;
			b = y;
		}
		
		public int getSize() {
			return 8;
		}
		
		public void assemble(Assembler asm, ByteArrayOutputStream outputStream) {
			String lhs;
			
			if (a.charAt(0) == '!') {
				String label = a.substring(1);
				
				if (!labels.containsKey(label)) {
					throw new RuntimeException("undefined label \"" + label + "\"");
				}
				
				lhs = "#" + labels.get(label);
			} else {
				lhs = a;
			}
			
			asm.processInstruction(lhs, b);
		}
	}
	
	class DataSegment implements Assemblee {
		byte[] data;
		
		public DataSegment(byte[] value) {
			data = value;
		}
		
		public int getSize() {
			return data.length;
		}
		
		public void assemble(Assembler asm, ByteArrayOutputStream outputStream) {
			outputStream.write(data, 0, data.length);
		}
	}
	
	private HashMap<String, Integer> labels;
	private ArrayList<Assemblee> instructions;
	private int currentOffset;
	private int stackHeight;
	private int nonce;
	
	public MagaratheaTarget() {
		labels = new HashMap<String, Integer>();
		instructions = new ArrayList<Assemblee>();
		nonce = 0;
		
		asm("#0", "jmp.branch");
		asm("#" + STACK_PTR_ADDRESS, "mem.addr");
		asm("#" + 0x5000, "mem.write");
		asm("!Kernel.initialize", "jmp.branch");
	}
	
	@Override
	public void emitFunctionEnter(String container, String identifier, int arguments, int registers) {
		emitLabel(container + identifier);
		
		stackHeight = registers;
	}
	
	public void execute() {
		ByteArrayOutputStream ramDestination = new ByteArrayOutputStream();
		
		Assembler assembler = new Assembler(ramDestination);
		
		for (Assemblee segment : instructions) {
			segment.assemble(assembler, ramDestination);
		}
		
		Runner.runWithBytecode(ramDestination.toByteArray());
	}
	
	protected void inst(Assemblee asm) {
		currentOffset += asm.getSize();
		instructions.add(asm);
	}
	
	protected void asm(String lhs, String rhs) {
		inst(new TupleSegment(lhs, rhs));
		System.out.println("\t" + lhs + "\t" + rhs);
	}
	
	protected String emitLabel() {
		String label = "NONCE" + (nonce++);
		
		emitLabel(label);
		return label;
	}
	
	protected void emitLabel(String label) {
		if (labels.containsKey(label)) {
			throw new RuntimeException("duplicate label \"" + label + "\"");
		} else {
			labels.put(label, currentOffset);
		}
	}
	
	protected void getRegister(int register) {
		asm("#" + STACK_PTR_ADDRESS, "mem.read");
		asm("mem.result", "alu.op");
		
		if (register > 0) {
			asm("#" + register, "alu.add");
		} else if (register < 0) {
			asm("#" + (-register), "alu.sub");
		}
		
		asm("alu.result", "mem.read");
		/* "mem.result"; */
		
		/* asm("#" + STACK_PTR, "mem.addr");
		asm("#0", "mem.read");
		asm("mem.result", "alu.operand");
		asm("#" + reg, "alu.add");
		asm("alu.result", "mem.addr");
		asm("#0", "mem.read");
		asm("#0"); */
	}
	
	protected void setRegister(int register) {
		asm("#" + STACK_PTR_ADDRESS, "mem.read");
		asm("mem.result", "alu.op");
		
		if (register > 0) {
			asm("#" + register, "alu.add");
		} else if (register < 0) {
			asm("#" + (-register), "alu.sub");
		}
		
		asm("alu.result", "mem.addr");
		
		/* asm("#" + STACK_PTR, "mem.addr");
		asm("#0", "mem.read");
		asm("mem.result", "alu.operand");
		asm("#" + register, "alu.add");
		asm("alu.result", "mem.addr");
		asm(value, "mem.write"); */
	}
	
	@Override
	public void emitConstantLoadInstruction(int target, int value) {
		setRegister(target);
		asm("#" + value, "mem.write");
	}
	
	@Override
	public void emitAddInstruction(int target, int a, int b) { }
	
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
		
		setRegister(target);
		getRegister(source);
		asm("mem.result", "mem.write");
	}
	
	@Override
	public void emitCallInstruction(int returnValue, String methodName, int target, int[] arguments) {
		if (methodName.equals("print")) {
			asm("#" + SCREEN_PTR, "mem.read");
			asm("mem.result", "alu.op");
			asm("#" + 0x8000, "alu.add");
			asm("alu.result", "mem.addr");
			getRegister(target);
			asm("mem.result", "mem.write");
			
			asm("#" + SCREEN_PTR, "mem.read");
			asm("mem.result", "alu.op");
			asm("#" + SCREEN_PTR, "mem.addr");
			asm("#1", "alu.add");
			asm("alu.result", "mem.write");
			
			/*
			asm("#" + 0x8000, "mem.addr");
			asm(result, "mem.write");
			*/
		} else if (methodName.equals("say_42")) {
			int value = nonce++;
			
			asm("#" + STACK_PTR_ADDRESS, "mem.read");
			asm("mem.result", "alu.op");
			asm("#" + (stackHeight + 1), "alu.add");
			asm("#" + STACK_PTR_ADDRESS, "mem.addr");
			asm("alu.result", "mem.write");
			asm("!Integer.say_42", "jmp.branch");
			
			setRegister(-1);
			asm("!RETURN" + value, "mem.write");
			
			emitLabel("RETURN" + value);
			
			asm("#" + STACK_PTR_ADDRESS, "mem.read");
			asm("mem.result", "alu.op");
			asm("#" + (stackHeight + 1), "alu.sub");
			asm("#" + STACK_PTR_ADDRESS, "mem.addr");
			asm("alu.result", "mem.write");
		} else if (methodName.equals("exit")) {
			String label = emitLabel();
			asm("!" + label, "jmp.branch");
		}
	}
	
	@Override
	public void emitReturn(int register) {
		getRegister(-1);
		asm("mem.result", "jmp.branch");
	}
	
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
		setRegister(register);
		asm("#0", "mem.write");
	}
	
	@Override
	public void emitSourceLine(String sourceFile, int lineNumber) {
		asm("#" + lineNumber, "alu.print");
	}
	
	@Override
	public void emitStateVariable(int register, int subject, String slotNumber) { }
	
	@Override
	public void emitWriteStateVariable(int subject, String slotNumber, int source) { }
}