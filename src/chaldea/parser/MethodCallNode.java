package chaldea.parser;

public class MethodCallNode extends Node {
	String methodName;
	Node subject;
	Node[] arguments;
	
	public MethodCallNode(String name, Node subj, Node[] args) {
		methodName = name;
		subject = subj;
		arguments = args;
	}
	
	public void walkChildren(TreeWalker<?> walker) {
		subject = walker.process(subject);
		
		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = walker.process(arguments[i]);
		}
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public Node getSubject() {
		return subject;
	}
	
	public Node[] getArguments() {
		return arguments;
	}
}