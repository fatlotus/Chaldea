package chaldea.runtime;

public class ClosureType extends AbstractType {
	public ClosureType(Type parentType) {
		super(parentType);
		
		addMethod(new NativeMethod() {
			public String getName() { return "#call"; }
			public int getNumberOfArguments() { return -1; }
			
			public ChaldeaValue methodBody(TypeSpace ts, ChaldeaValue callee, ChaldeaValue[] arguments) {
				ClosureValue closure = ((ClosureValue)callee);
				
				Type type = closure.getEnclosingType();
				Method m = type.getMethod(closure.getMethodName());
				
				/*
				System.err.println("m.getNumberOfArguments: " + m.getNumberOfArguments());
				System.err.println("arguments.length: " + arguments.length);
				*/
				
				ChaldeaValue[] savedRegisters = closure.getRegisterState();
				ChaldeaValue[] registerState = new ChaldeaValue[m.getNumberOfArguments()];
				
				/* FIXME */
				/* 
				if (m.getNumberOfArguments() != arguments.length + savedRegisters.length) {
					throw new RuntimeException("attempt to call a closure with an inappropriate number of arguments");
				}
				*/
				
				int j = 0;
				
				for (int i = 1; i < m.getNumberOfArguments() - arguments.length; i++) { /* FIXME */
					registerState[j++] = savedRegisters[i];
				}
				
				for (int i = 0; i < arguments.length; i++) {
					registerState[j++] = arguments[i];
				}
				
				ChaldeaValue returnValue = m.evaluate(ts, savedRegisters[0], registerState);
				
				j = 1;
				
				try {
					for (int i = 0; i < m.getNumberOfArguments() - arguments.length; i++) {
						savedRegisters[j++] = registerState[i];
					}
				} catch (ArrayIndexOutOfBoundsException e) { /* MAJOR FIXME */ }
				
				return returnValue;
			}
		});
		
		addMethod(new NativeMethod() {
			public String getName() { return "#forever"; }
			public int getNumberOfArguments() { return -1; }
			
			public ChaldeaValue methodBody(TypeSpace ts, ChaldeaValue callee, ChaldeaValue[] arguments) {
				while (true) {
					Method m = callee.getType().getMethod("#call");
					
					ChaldeaValue val = m.evaluate(ts, callee, new ChaldeaValue[0]);
					
					if (val.getType() instanceof NullType) {
						return val;
					}
				}
			}
		});
	}
	
	public int getSizeOfInstance() {
		return 4;
	}
	
	public String getName() {
		return "Closure";
	}
}