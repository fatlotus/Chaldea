package chaldea.runtime;

public abstract class AbstractValue implements ChaldeaValue {
	private ChaldeaValue[] state;
	
	public AbstractValue() {
		if (getType() == null || getType().getSizeOfInstance() < 0) {
			state = new ChaldeaValue[0];
		} else {
			state = new ChaldeaValue[getType().getSizeOfInstance()];
		}
	}
	
	public abstract Type getType();
	
	public void setStateVariable(int slotNumber, ChaldeaValue val) {
		state[slotNumber] = val;
	}
	
	public ChaldeaValue getStateVariable(int slotNumber) {
		return state[slotNumber];
	}
}