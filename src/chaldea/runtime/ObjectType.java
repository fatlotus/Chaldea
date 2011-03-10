package chaldea.runtime;

import chaldea.CompilerTarget;

public class ObjectType extends AbstractType {
	public ObjectType() {
		super(null);
		
		addMethod(new NativeMethod() {
			public String getName() { return ".new"; }
			public int getNumberOfArguments() { return -1; }
			
			public ChaldeaValue methodBody(TypeSpace ts, final ChaldeaValue callee, ChaldeaValue[] arguments) {
				ChaldeaValue newInstance = new AbstractValue() {
					public String toString() {
						return "<instance of " + getType() + ">";
					}
					public Type getType() {
						return ((ReflectedValue)callee).getRepresentingType();
					}
				};
				
				newInstance.getType().getMethod("#initialize").evaluate(ts, newInstance, arguments);
				
				return newInstance;
			}
		});
		
		addMethod(new AbstractMethod() {
			public String getDeclaredContainer() { return "Object"; }
			public String getName() { return "#initialize"; }
			public int getNumberOfArguments() { return 0; }
			public int getNumberOfRegisters() { return 1; }
			protected void methodBody(CompilerTarget target) {
				target.emitConstantLoadInstruction(0, 0); /* FIXME */
				target.emitReturn(0);
			}
		});
		
		addMethod(new NativeMethod() {
			public String getName() { return "#print"; }
			public int getNumberOfArguments() { return 0; }
			
			public ChaldeaValue methodBody(TypeSpace ts, final ChaldeaValue callee, ChaldeaValue[] arguments) {
				System.out.println(callee.toString());
				
				return ts.getNullValue();
			}
		});
		
		addMethod(new NativeMethod() {
			public String getName() { return "#convert"; }
			public int getNumberOfArguments() { return 1; }
			
			public ChaldeaValue methodBody(TypeSpace ts, ChaldeaValue callee, ChaldeaValue[] arguments) {
				if (((ReflectedValue)arguments[0]).getRepresentingType().getName() == "String") {
					return ts.wrap(callee.toString());
				}
				
				return ts.getNullValue();
			}
		});
		
		addMethod(new NativeMethod() {
			public String getName() { return "#eq"; }
			public int getNumberOfArguments() { return 1; }
			
			public ChaldeaValue methodBody(TypeSpace ts, ChaldeaValue callee, ChaldeaValue[] arguments) {
				if (callee == arguments[0]) { /* only exact reference matches, by default */
					return ts.wrap(0);
				} else {
					return ts.getNullValue();
				}
			}
		});
	}
	
	public int getSizeOfInstance() {
		return 4; // type of object, as an ID.
	}
	
	/*
	public boolean definesMethod(String methodName) {
		return super.definesMethod(methodName) || methodName.equals(".new");
	}
	
	public void methodDispatch(String methodName, CompilerTarget t) {
		boolean isNativeMethod = methodName.equals("new");
		
		if (super.definesMethod(methodName) || !isNativeMethod) {
			super.methodDispatch(methodName, t);
		} else {
			throw new NativeMethodException(getName(), methodName);
		}
	}
	*/
	
	public String getName() {
		return "Object";
	}
}