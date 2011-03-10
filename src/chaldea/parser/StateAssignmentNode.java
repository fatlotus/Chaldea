package chaldea.parser;

public class StateAssignmentNode extends Node {
	private String variableName;
	private Node rightHandSide;
	
	public StateAssignmentNode(String name, Node value) {
		variableName = name;
		rightHandSide = value;
	}
	
	public void walkChildren(TreeWalker<?> node) {
		rightHandSide = node.process(rightHandSide);
	}
}