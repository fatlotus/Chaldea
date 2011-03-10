package chaldea.parser;

public class IntegerNode extends Node {
	int value;
	
	public IntegerNode(int v) {
		value = v;
	}
	
	public int getValue() { return value; }
	
	void walkChildren(TreeWalker<?> walker) { }
}