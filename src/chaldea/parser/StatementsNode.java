package chaldea.parser;

import java.util.List;

public class StatementsNode extends Node {
	private Node[] statements;
	
	public StatementsNode(List<Node> stmts) {
		this(stmts.toArray(new Node[0]));
	}
	
	public StatementsNode(Node[] stmts) {
		statements = stmts;
	}
	
	public Node[] getChildren() {
		return statements;
	}
	
	public Node getLastChild() {
		return statements[statements.length - 1];
	}
	
	public void walkChildren(TreeWalker<?> treeWalker) {
		for (int i = 0; i < statements.length; i++) {
			statements[i] = treeWalker.process(statements[i]);
		}
	}
}