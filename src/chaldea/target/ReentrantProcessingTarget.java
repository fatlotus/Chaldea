package chaldea.target;

import chaldea.CompilerTarget;
import chaldea.runtime.Method;
import chaldea.runtime.AbstractMethod;
import chaldea.runtime.ReturnException;

import java.util.Stack;
import java.util.List;

public class ReentrantProcessingTarget implements CompilerTarget {
	private String methodName;
	private String methodContainer;
	private int methodArguments;
	private int methodRegisters;
	private Stack<Operation> beforeCurrent;
	private Stack<Operation> afterCurrent;
	private CompilerTarget controllerTarget;
	private CompilerTarget outputTarget;
	
	private Stack<State> stateStack;
	
	private class State {
		public State() {
			name = methodName;
			container = methodContainer;
			arguments = methodArguments;
			registers = methodRegisters;
			
			before = beforeCurrent;
			after = afterCurrent;
		}
		
		public void restore() {
			setMethodName(name);
			setMethodContainer(container);
			setMethodArgumentNum(arguments);
			setMethodRegisterNum(registers);
			
			beforeCurrent = before;
			afterCurrent = after;
		}
		
		private Stack<Operation> before;
		private Stack<Operation> after;
		private String name;
		private String container;
		private int arguments;
		private int registers;
	}
	
	public static interface OperationList {
		public void run(CompilerTarget x);
	}
	
	private static abstract class Operation {
		private Object annotation;
		
		public Operation() { }
		
		public abstract void run(CompilerTarget t);
		
		public Object getAnnotation() { return annotation; }
		public void setAnnotation(Object anno) { annotation = anno; }
	}
	
	public ReentrantProcessingTarget(CompilerTarget controller, CompilerTarget output) {
		controllerTarget = controller;
		outputTarget = output;
		beforeCurrent = new Stack<Operation>();
		afterCurrent = new Stack<Operation>();
		stateStack = new Stack<State>();
	}
	
	protected Method getAsMethod() {
		while(!afterCurrent.empty())
			beforeCurrent.push(afterCurrent.pop());
		
		final List<Operation> operations = (List<Operation>)beforeCurrent.clone();
		final String name = methodName;
		final String container = methodContainer;
		final int registers = methodRegisters;
		final int arguments = methodArguments;
		
		return new AbstractMethod() {
			public String getDeclaredContainer() {
				return container;
			}
			
			public String getName() {
				return name;
			}
			
			public int getNumberOfRegisters() {
				return registers;
			}
			
			public int getNumberOfArguments() {
				return arguments;
			}
			
			public void methodBody(CompilerTarget x) {
				for (Operation o : operations) {
					o.run(x);
				}
			}
		};
	}
	
	public ReentrantProcessingTarget(CompilerTarget output) {
		this(null, output);
	}
	
	public void clearOperationsList() {
		beforeCurrent = new Stack<Operation>();
		afterCurrent = new Stack<Operation>();
	}
	
	public void emitOperationsList() {
		while (!afterCurrent.empty())
			beforeCurrent.push(afterCurrent.pop());
		
		outputTarget.emitFunctionEnter(methodContainer, methodName, methodArguments, methodRegisters);
		for (Operation o : beforeCurrent) {
			o.run(outputTarget);
		}
		outputTarget.emitFunctionExit();
	}
	
	public void setAnnotation(Object anno) {
		beforeCurrent.peek().setAnnotation(anno);
	}
	
	public Object getAnnotation() {
		return beforeCurrent.peek().getAnnotation();
	}
	
	public void setController(CompilerTarget target) {
		controllerTarget = target;
	}
	
	public void remove() {
		beforeCurrent.pop();
	}
	
	public int getCurrentOperationNumber() {
		return beforeCurrent.size();
	}
	
	public boolean hasNext() {
		return !afterCurrent.empty();
	}
	
	public boolean hasPrevious() {
		return !beforeCurrent.empty();
	}
	
	public void next() {
		Operation o = afterCurrent.pop();
		beforeCurrent.push(o);
		
		o.run(controllerTarget);
	}

	public void previous() {
		Operation o = beforeCurrent.peek();

		o.run(controllerTarget);
		
		beforeCurrent.pop();
		afterCurrent.push(o);
	}
	
	public String getMethodContainer() {
		return methodContainer;
	}
	
	public void setMethodContainer(String container) {
		methodContainer = container;
	}
	
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String newName) {
		methodName = newName;
	}
	
	public int getMethodRegisterNum() {
		return methodRegisters;
	}
	
	public void setMethodRegisterNum(int newNumber) {
		methodRegisters = newNumber;
	}
	
	public int getMethodArgumentNum() {
		return methodArguments;
	}
	
	public void setMethodArgumentNum(int newNumber) {
		methodArguments = newNumber;
	}
	
	protected void pushState() {
		stateStack.push(new State());
	}
	
	protected void popState() {
		if (!stateStack.empty()) {
			stateStack.pop().restore();
		}
	}
	
	@Override
	public void emitFunctionEnter(String container, String name, int numArguments, int numRegisters) {
		pushState();
		
		setMethodName(name);
		setMethodContainer(container);
		setMethodRegisterNum(numRegisters);
		setMethodArgumentNum(numArguments);
		
		clearOperationsList();
		
		controllerTarget.emitFunctionEnter(container, methodName, numArguments, numRegisters);
	}
	
	@Override
	public void emitFunctionExit() {
		while (!beforeCurrent.empty())
			afterCurrent.push(beforeCurrent.pop());
		
		controllerTarget.emitFunctionExit();
		
		popState();
	}
	
	@Override
	public void emitConstantLoadInstruction(final int target, final int value) {
		beforeCurrent.push(new Operation() {
			public void run(CompilerTarget x) {
				x.emitConstantLoadInstruction(target, value);
			}
		});
	}
	
	@Override
	public void emitAddInstruction(final int target, final int a, final int b) {
		beforeCurrent.push(new Operation() {
			public void run(CompilerTarget x) {
				x.emitAddInstruction(target, a, b);
			}
		});
	}
	
	@Override
	public void emitCallInstruction(final int returnValue, final String methodName, final int target, final int[] arguments) {
		beforeCurrent.push(new Operation() {
			public void run(CompilerTarget x) {
				x.emitCallInstruction(returnValue, methodName, target, arguments);
			}
		});
	}
	
	@Override
	public void emitCopyInstruction(final int target, final int source) {
		beforeCurrent.push(new Operation() {
			public void run(CompilerTarget x) {
				x.emitCopyInstruction(target, source);
			}
		});
	}
	
	@Override
	public void emitReturn(final int register) {
		beforeCurrent.push(new Operation() {
			public void run(CompilerTarget x) {
				x.emitReturn(register);
			}
		});
	}
	
	@Override
	public void emitLoadConstantClass(final int target, final String name) {
		beforeCurrent.push(new Operation() {
			public void run(CompilerTarget x) {
				x.emitLoadConstantClass(target, name);
			}
		});
	}
	
	@Override
	public void emitLoadClosure(final int target, final int closure) {
		beforeCurrent.push(new Operation() {
			public void run(CompilerTarget x) {
				x.emitLoadClosure(target, closure);
			}
		});
	}

	@Override
	public void emitLoadConstantString(final int target, final String value) {
		beforeCurrent.push(new Operation() {
			public void run(CompilerTarget x) {
				x.emitLoadConstantString(target, value);
			}
		});
	}
	
	@Override
	public void emitLoadNull(final int target) {
		beforeCurrent.push(new Operation() {
			public void run(CompilerTarget x) {
				x.emitLoadNull(target);
			}
		});
	}
	
	@Override
	public void emitSourceLine(final String sourceFile, final int lineNumber) {
		beforeCurrent.push(new Operation() {
			public void run(CompilerTarget x) {
				x.emitSourceLine(sourceFile, lineNumber);
			}
		});
	}
	
	@Override
	public void emitStateVariable(final int register, final int subject, final String slotNumber) {
		beforeCurrent.push(new Operation() {
			public void run(CompilerTarget x) {
				x.emitStateVariable(register, subject, slotNumber);
			}
		});
	}
	
	@Override
	public void emitWriteStateVariable(final int subject, final String slotNumber, final int source) {
		beforeCurrent.push(new Operation() {
			public void run(CompilerTarget x) {
				x.emitWriteStateVariable(subject, slotNumber, source);
			}
		});
	}
	
	@Override
	public void emitTailCallInstruction(final String method, final int callee, final int[] arguments) {
		beforeCurrent.push(new Operation() {
			public void run(CompilerTarget x) {
				x.emitTailCallInstruction(method, callee, arguments);
			}
		});
	}
}