package chaldea.parser;

public class StringNode extends Node {
	private String value;
	
	public StringNode(String v) {
		value = v;
	}
	
	public String getValue() {
		return value;
	}
	
	public void walkChildren(TreeWalker<?> walker) { }
}