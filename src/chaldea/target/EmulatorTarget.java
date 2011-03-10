package chaldea.target;

import chaldea.CompilerTarget;
import chaldea.runtime.TypeSpace;
import chaldea.runtime.Type;
import chaldea.runtime.ChaldeaValue;
import chaldea.runtime.IntegerValue;
import chaldea.runtime.ReturnException;
import chaldea.runtime.ReflectedValue;
import chaldea.runtime.Method;
import chaldea.runtime.ChaldeaRuntimeError;
import chaldea.runtime.ClosureValue;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class EmulatorTarget extends CompatCompilerTarget {
	private ChaldeaValue[] registers;
	private String typeName;
	private String methodName;
	private Stack<ChaldeaValue[]> registerStateStack;
	private TypeSpace typeSpace;
	private Stack<ChaldeaValue> returnValues;
	private String sourceFile;
	private int lineNumber;
	
	public EmulatorTarget(TypeSpace t) {
		registers = null;
		methodName = null;
		typeName = null;
		typeSpace = t;
	}
	
	public void setRegister(int register, ChaldeaValue value) {
		registers[register] = value;
	}
	
	public ChaldeaValue getRegister(int register) {
		return registers[register];
	}
	
	@Override
	public void emitFunctionEnter(String container, String identifier, int numArguments, int numRegisters) {
		methodName = identifier;
		typeName = container;
		registers = new ChaldeaValue[numRegisters];
	}
	
	@Override
	public void emitFunctionExit() {
		/* does nothing */
	}
	
	@Override
	public void emitConstantLoadInstruction(int target, int value) {
		registers[target] = typeSpace.wrap(value);
	}
	
	@Override
	public void emitCopyInstruction(int target, int source) {
		registers[target] = registers[source];
	}
	
	@Override
	public void emitLoadConstantClass(int target, String name) {
		registers[target] = typeSpace.reflectOn(name);
	}
	
	@Override
	public void emitLoadConstantString(int target, String value) {
		registers[target] = typeSpace.wrap(value);
	}
	
	@Override
	public void emitCallInstruction(int returnValue, String methodName, int subject, int[] argumentRegisters) {
		ChaldeaValue[] arguments = new ChaldeaValue[argumentRegisters.length];
		ChaldeaValue callee = registers[subject];
		
		Type type = callee.getType();
		boolean singletonMethod;
		
		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = registers[argumentRegisters[i]];
		}
		
		if (callee instanceof ReflectedValue) { /* FIXME */
			type = ((ReflectedValue)callee).getRepresentingType();
			singletonMethod = true;
		} else {
			singletonMethod = false;
		}
		
		if (type == null) {
			throw new ChaldeaRuntimeError("untyped object encountered: " + callee);
		}
		
		Method m = type.getMethod((singletonMethod ? '.' : '#') + methodName);
		
		if (m == null) {
			String prettyName = (singletonMethod ? '+' : '-') + " " + methodName;
			
			throw new ChaldeaRuntimeError("undefined method " + prettyName + " of type " + type);
		} else {
			registers[returnValue] = m.evaluate(typeSpace, callee, arguments);
			
			if (registers[returnValue] == null) {
				throw new NullPointerException("a method has returned null!");
			}
		}
	}
	
	@Override
	public void emitReturn(int register) {
		throw new ReturnException(registers[register]);
	}
	
	@Override
	public void emitLoadClosure(int register, int closureNumber) {
		final String closureName = methodName + "-" + closureNumber;
		final String closureTypeName = typeName;
		final ChaldeaValue[] registersCopy = registers;
		
		ChaldeaValue closure = new ClosureValue(typeSpace) {
			public ChaldeaValue[] getRegisterState() {
				return registersCopy;
			}
			
			public String getTypeName() {
				return closureTypeName;
			}
			
			public String getMethodName() {
				return closureName;
			}
		};
		
		registers[register] = closure;
	}
	
	@Override
	public void emitSourceLine(String name, int line) {
		sourceFile = name;
		lineNumber = line;
	}
	
	@Override
	public void emitLoadNull(int register) {
		registers[register] = typeSpace.getNullValue();
	}
	
	@Override
	public void emitStateVariable(int register, int subject, String slotNumber) {
		registers[register] = registers[subject].getStateVariable(Integer.parseInt(slotNumber));
	}
	
	@Override
	public void emitWriteStateVariable(int subject, String slotNumber, int source) {
		registers[subject].setStateVariable(Integer.parseInt(slotNumber), registers[source]);
	}
}