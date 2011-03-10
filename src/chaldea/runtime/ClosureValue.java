package chaldea.runtime;

public abstract class ClosureValue extends AbstractValue {
	private TypeSpace typeSpace;
	private Type closureType;
	
	public ClosureValue(TypeSpace ts) {
		typeSpace = ts;
		closureType = ts.getClosureType();
	}
	
	public abstract ChaldeaValue[] getRegisterState();
	public abstract String getTypeName();
	public abstract String getMethodName();
	
	public String toString() {
		return "<closure " + getTypeName() + getMethodName() + ">";
	}
	
	public Type getEnclosingType() {
		return typeSpace.getTypeWithName(getTypeName());
	}
	
	public Type getType() {
		return closureType;
	}
}