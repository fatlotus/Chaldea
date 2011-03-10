package chaldea.parser;

public class LocalVariableNode extends Node {
	private String variableName;
	private int registerNumber;
	
	public LocalVariableNode(String name, int register) {
		variableName = name;
		registerNumber = register;
	}
	
	public String getVariableName() {
		return variableName;
	}
	
	public int getRegisterNumber() {
		return registerNumber;
	}
}