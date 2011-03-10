package chaldea.runtime;

import chaldea.CompilerTarget;

public class MetaType extends AbstractType {
	public MetaType(ObjectType parent) {
		super(parent);
	}
	
	public String getName() {
		return "Type";
	}
	
	public int getSizeOfInstance() {
		return 0;
	}
	
	public void methodDispatch(String methodName, CompilerTarget t) {
		System.err.println("oops!");
	}
}