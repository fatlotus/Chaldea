package chaldea.target;

import chaldea.CompilerTarget;
import chaldea.runtime.TypeSpace;
import chaldea.runtime.Type;
import chaldea.runtime.AbstractType;
import chaldea.runtime.Method;

import java.util.Stack;
import java.util.HashMap;

public class LinkerTarget extends ReentrantProcessingTarget {
	private TypeSpace typeSpace;
	private LinkerTarget outerClass;
	
	private class Controller extends NullTarget {
		private Type currentType;
		
		public void emitFunctionEnter(String container, String identifier, int arguments, int registers) {
			/* TODO: Looks for closures already defined and renames this method. */
		}
		
		/* FIXME: methods can define ivars outside of constructor */
		@Override
		public void emitStateVariable(int register, int subject, String variableName) {
			String name;
			
			if (subject != 0)
				throw new RuntimeException("subject must be zero");
			
			if (Character.isDigit(variableName.charAt(0)))
				return;
			
			if (variableName.charAt(0) != '@')
				throw new IllegalArgumentException("invalid instance variable; names must start with an at-sign");
			
			if (variableName.charAt(1) == '@') { // static variable
				name = variableName.substring(2);
				
				throw new RuntimeException("static variables not yet supported...");
			} else {
				name = variableName.substring(1);
				
				int variableNumber = currentType.defineInstanceVariable(variableName);
				
				remove();
				
				outerClass.emitStateVariable(register, subject, Integer.toString(variableNumber));
			}
		}
		
		@Override
		public void emitWriteStateVariable(int subject, String variableName, int source) {
			String name;
			
			if (Character.isDigit(variableName.charAt(0)))
				return;
			
			if (subject != 0)
				throw new RuntimeException("subject must be zero");
			
			if (variableName.charAt(0) != '@')
				throw new IllegalArgumentException("invalid instance variable; names must start with an at-sign");
			
			if (variableName.charAt(1) == '@') { // static variable
				name = variableName.substring(2);
				
				throw new RuntimeException("static variables not yet supported...");
			} else {
				name = variableName.substring(1);
				
				int variableNumber = currentType.defineInstanceVariable(variableName);
				
				remove();
				
				outerClass.emitWriteStateVariable(subject, Integer.toString(variableNumber), source);
			}
		}
		
		@Override
		public void emitFunctionExit() {
			final String name = getMethodContainer();
			
			Type t = typeSpace.getTypeWithName(name);
			
			if (t == null) {
				t = new AbstractType(typeSpace.getObjectType()) {
					private int size = 0;
					private HashMap<String, Integer> instanceVariables = new HashMap<String, Integer>();
					
					@Override
					public String getName() {
						return name;
					}
					
					@Override
					public int getSizeOfInstance() {
						return size;
					}
					
					@Override
					public int defineInstanceVariable(String name) {
						if (instanceVariables.containsKey(name)) {
							return instanceVariables.get(name);
						} else {
							int number = size++;
							instanceVariables.put(name, number);
							return number;
						}
					}
					
				};
				
				typeSpace.addType(t);
			}
			
			Type savedType = currentType;
			currentType = t;
			
			while (hasNext()) { next(); } // loop over and do some simple optimizations
			
			Method method = getAsMethod();
			
			t.addMethod(method);
			
			currentType = savedType;
		}
	}
	
	public LinkerTarget(TypeSpace space) {
		super(null, null);
		
		setController(new Controller());
		
		typeSpace = space;
		outerClass = this;
	}
}