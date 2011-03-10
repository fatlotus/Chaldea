package chaldea.parser;

import java.util.Stack;

public abstract class TreeWalker<T extends Node> {
	private Stack<Node> replacementStack;
	
	public TreeWalker() {
		replacementStack = new Stack<Node>();
	}
	
	public void resetRegisters() { } /* FIXME */
	
	public int reserveRegister() { return -1; }
	
	protected boolean isPreorderTraversal() { return false; }
	
	protected void replace(T newValue) {
		replacementStack.pop();
		replacementStack.push(newValue);
	}
	
	protected T processEntireMethod(String container, String methodName,
	                                int arguments, Node n) {
		T result = process(n);
		return result;
	}
	
	protected T process(Node n) {
		replacementStack.push(n);
		
		boolean isPreorder = isPreorderTraversal();
		
		if (!isPreorder) {
			replacementStack.peek().walkChildren(this);
		}
		
		if (n instanceof IntegerNode) {
			walk((IntegerNode)n);
		} else if (n instanceof StatementsNode) {
			walk((StatementsNode)n);
		} else if (n instanceof MethodCallNode) {
			walk((MethodCallNode)n);
		} else if (n instanceof AssignmentNode) {
			walk((AssignmentNode)n);
		} else if (n instanceof LocalVariableNode) {
			walk((LocalVariableNode)n);
		} else if (n instanceof StateAssignmentNode) {
			walk((StateAssignmentNode)n);
		} else if (n instanceof StateVariableNode) {
			walk((StateVariableNode)n);
		} else if (n instanceof ConstantNode) {
			walk((ConstantNode)n);
		} else if (n instanceof StringNode) {
			walk((StringNode)n);
		} else if (n instanceof BlockNode) {
			walk((BlockNode)n);
		} else if (n instanceof NullNode) {
			walk((NullNode)n);
		} else {
			walk(n);
		}
		
		if (isPreorder) {
			replacementStack.peek().walkChildren(this);
		}
		
		return (T)replacementStack.pop();
	}
	
	protected void walk(Node n) {
		throw new UnsupportedOperationException(
		  "This TreeWalker doesn't support scanning " + n.getClass().getName() + " nodes.");
	}
	protected void walk(AssignmentNode n) { }
	protected void walk(LocalVariableNode n) { }
	protected void walk(IntegerNode n) { }
	protected void walk(StatementsNode n) { }
	protected void walk(MethodCallNode n) { }
	protected void walk(ConstantNode n) { }
	protected void walk(StringNode n) { }
	protected void walk(NullNode n) { }
	protected void walk(BlockNode blk) { }
}
