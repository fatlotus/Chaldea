package chaldea.runtime;

public interface ChaldeaValue {
	Type getType();
	void setStateVariable(int slotNumber, ChaldeaValue val);
	ChaldeaValue getStateVariable(int slotNumber);
}