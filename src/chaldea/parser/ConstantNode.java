package chaldea.parser;

public class ConstantNode extends Node {
	private String constantName;
	
	public ConstantNode(String name) {
		constantName = name;
	}
	
	public String getName() {
		return constantName;
	}
	
	void walkChildren(TreeWalker<?> walker) { }
}