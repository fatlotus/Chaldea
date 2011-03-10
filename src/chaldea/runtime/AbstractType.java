package chaldea.runtime;

import chaldea.CompilerTarget;

import java.util.Map;
import java.util.HashMap;

public abstract class AbstractType implements Type {
	private int staticSegmentSize;
	private Type superType;
	private Map<String, Method> methods;
	private Map<String, Integer> staticVariables;
	
	public AbstractType(Type typeSuperType) {
		staticSegmentSize = 0;
		methods = new HashMap<String, Method>();
		superType = typeSuperType;
	}
	
	public abstract String getName();
	public abstract int getSizeOfInstance();
	public int defineInstanceVariable(String instanceVariableName) {
		throw new RuntimeException("cannot define new instance variable in " + this);
	}
	
	public int addStaticVariable(int size) {
		int offset = staticSegmentSize;
		staticSegmentSize += size;
		return offset;
	}
	
	@Override
	public int getSizeOfStaticSegment() {
		return staticSegmentSize;
	}
	
	@Override
	public void addMethod(Method func) {
		if (methods.containsKey(func.getName())) {
			// FIXME : "super" ignored.
		}
		
		methods.put(func.getName(), func);
	}
	
	@Override
	public boolean definesMethod(String methodName) {
		return methods.containsKey(methodName);
	}
	
	@Override
	public void methodDispatch(String methodName, CompilerTarget t) {
		if (definesMethod(methodName)) {
			Method func = getMethod(methodName);
			func.evaluateInto(t);
		} else {
			throw new ChaldeaRuntimeError("undefined method " + methodName + " of type " + getName());
		}
	}
	
	@Override
	public Method getMethod(String methodName) {
		if (definesMethod(methodName)) {
			return methods.get(methodName);
		} else if (superType != null) {
			return superType.getMethod(methodName);
		} else {
			return null;
		}
	}
	
	public String toString() {
		StringBuilder output = new StringBuilder();
		
		output.append("<type ");
		output.append(getName());
		output.append(", size=");
		output.append(Integer.toString(getSizeOfInstance()));
		
		for (Method method : methods.values()) {
			output.append(", ");
			output.append(method.getName()); 
		}
		
		output.append(">");
		
		return output.toString();
	}
}