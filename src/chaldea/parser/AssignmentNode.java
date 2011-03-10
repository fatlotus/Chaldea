package chaldea.parser;

public class AssignmentNode extends Node {
	String variableName;
	Node value;
	int registerNumber;
	
	public AssignmentNode(String n, Node v, int register) {
		variableName = n;
		value = v;
		registerNumber = register;
	}
	
	public String getVariableName() {
		return variableName;
	}
	
	public int getRegisterNumber() {
		return registerNumber;
	}
	
	public void setRegisterNumber(int register) {
		registerNumber = register;
	}
	
	public Node getRightHandSide() {
		return value;
	}
	
	void walkChildren(TreeWalker<?> walker) {
		value = walker.process(value);
	}
}