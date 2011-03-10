package chaldea.target;

import chaldea.CompilerTarget;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class OptimizeTarget extends ReentrantProcessingTarget {
	private ArrayList<Value> registerMapping;
	private OptimizeTarget thith;
	private String lastSourceFile;
	private int lastSourceLine;
	
	private interface Value {
		public boolean isConstant();
		public int getLastTimeUsed();
		public void setLastTimeUsed(int time);
	}
	
	private class ConstantValue implements Value {
		private int value;
		private int lastTimeUsed;
		
		public ConstantValue(int v) {
			value = v;
		}
		
		public int hashCode() {
			return value;
		}
		
		public boolean equals(Object other) {
			if (other instanceof ConstantValue) {
				return ((ConstantValue)other).value == value;
			} else {
				return false;
			}
		}
		
		public String toString() {
			return "#" + value;
		}
		
		public boolean isConstant() {
			return true;
		}
		
		public void setLastTimeUsed(int time) {
			lastTimeUsed = time;
		}
		
		public int getLastTimeUsed() {
			return lastTimeUsed;
		}
	}
	
	private class NullValue implements Value {
		private int lastTimeUsed;
		
		public NullValue() {
			lastTimeUsed = 0;
		}
		
		public int hashCode() { return 0; }
		public boolean equals(Object other) { return (other instanceof NullValue); }
		
		public void setLastTimeUsed(int time) { lastTimeUsed = time; }
		public int getLastTimeUsed() { return lastTimeUsed; }
		public boolean isConstant() { return true; }
	}
	
	private class UniqueValue implements Value {
		private String description;
		private int lastTimeUsed;
		
		public UniqueValue(String desc) {
			description = desc;
		}
		
		public int hashCode() {
			return super.hashCode();
		}
		
		public boolean equals(Object other) {
			return other == this;
		}
		
		public String toString() {
			return description;
		}
		
		public boolean isConstant() {
			return false;
		}
		
		public int getLastTimeUsed() {
			return lastTimeUsed;
		}
		
		public void setLastTimeUsed(int time) {
			lastTimeUsed = time;
		}
	}
	
	private class Callbacks implements CompilerTarget {
		protected void touch(int reg) {
			/*
			if (registerMapping.get(reg).getLastTimeUsed() < getCurrentOperationNumber()) {
				registerMapping.get(reg).setLastTimeUsed(getCurrentOperationNumber());
			}
			*/
			
			while(registerMapping.size() < reg + 1) {
				registerMapping.add(null);
			}
		}
		
		@Override
		public void emitFunctionEnter(String container, String methodName, int arguments, int registers) {
			lastSourceFile = null;
			lastSourceLine = -1;
			
			touch(0); // self is automatically written when the method is entered.
			
			for (int i = 0; i < arguments; i++) {
				touch(i + 1);
			}
		}
		
		@Override
		public void emitConstantLoadInstruction(int target, int value) {
			touch(target);
			
			registerMapping.set(target, new ConstantValue(value));
		}
		
		@Override
		public void emitAddInstruction(int target, int a, int b) {
			touch(target);
			
			if (registerMapping.get(a).isConstant() && registerMapping.get(b).isConstant()) {
				thith.remove();
				thith.emitConstantLoadInstruction(target, a + b);
				
				registerMapping.set(target, new ConstantValue(a + b));
			} else {
				registerMapping.set(target, new UniqueValue(a + " + " + b));
			}
		}
		
		@Override
		public void emitCallInstruction(int returnValue, String methodName, int target, int[] arguments) {
			touch(returnValue);
			
			registerMapping.set(returnValue, new UniqueValue(registerMapping.get(target) + "#" + methodName));
		}
		
		@Override
		public void emitCopyInstruction(int target, int source) {
			touch(target);
			
			registerMapping.set(target, registerMapping.get(source));
		}
		
		@Override
		public void emitReturn(int register) {
			/* do nothing, yet */
		}
		
		@Override
		public void emitLoadConstantClass(int target, String name) {
			touch(target);
			
			registerMapping.set(target, new UniqueValue("c" + name));
		}

		@Override
		public void emitLoadConstantString(int target, String value) {
			touch(target);
			
			registerMapping.set(target, new UniqueValue("\"" + value + "\""));
		}
		
		@Override
		public void emitLoadClosure(int target, int closure) {
			touch(target);
			
			registerMapping.set(target, new UniqueValue("closure $" + closure));
		}
		
		@Override
		public void emitLoadNull(int target) {
			touch(target);
			
			registerMapping.set(target, new NullValue());
		}

		@Override
		public void emitStateVariable(int register, int subject, String variableName) {
			touch(register);
			
			registerMapping.set(register, new UniqueValue(registerMapping.get(subject) + ".@" + variableName));
		}

		@Override
		public void emitWriteStateVariable(int subject, String variableName, int source) {
			touch(subject);
		}
		
		@Override
		public void emitSourceLine(String sourceFile, int lineNumber) {
			if (sourceFile.equals(lastSourceFile) && lastSourceLine == lineNumber) {
				remove();
			}
			
			lastSourceFile = sourceFile;
			lastSourceLine = lineNumber;
		}
		
		@Override
		public void emitTailCallInstruction(String method, int callee, int[] arguments) {
			touch(callee);
		}
		
		@Override
		public void emitFunctionExit() {
			while (hasNext())
				next();
			
			setMethodRegisterNum(registerMapping.size());
			emitOperationsList();
			
			registerMapping = new ArrayList<Value>();
		}
	}
	
	public OptimizeTarget(CompilerTarget output) {
		super(output);
		
		registerMapping = new ArrayList<Value>();
		thith = this;
		
		setController(new Callbacks());
	}
}