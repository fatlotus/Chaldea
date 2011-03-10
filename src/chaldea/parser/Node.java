package chaldea.parser;

public abstract class Node {
	public Node() { }
	
	void walkChildren(TreeWalker<?> walker) { }
}