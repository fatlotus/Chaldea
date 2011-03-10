package chaldea.parser;

public class BlockNode extends Node {
	private Node childNode;
	private int numArguments;
	
	public BlockNode(Node child, int arguments) {
		childNode = child;
		numArguments = arguments;
	}
	
	public int getNumberOfArguments() {
		return numArguments;
	}
	
	public Node getChildNode() {
		return childNode;
	}
	
	void walkChildren(TreeWalker<?> walk) { } /* not considered part of method */
	
	public void actuallyWalkChildren(TreeWalker<?> walk) { /* FIXME */
		childNode = walk.process(childNode);
	}
}