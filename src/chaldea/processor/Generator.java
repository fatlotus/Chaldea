package chaldea.processor;

import chaldea.parser.*;
import chaldea.CompilerTarget;
import chaldea.target.OptimizeTarget;

import java.util.Stack;

public class Generator extends TreeWalker<Generator.RegisterNode> {
	private int registerNumber;
	private int localVariables;
	private String currentContainer;
	private String currentName;
	private int currentArguments;
	private int nextClosureNumber;
	private CompilerTarget target;
	
	class RegisterNode extends Node {
		private int reg;
		
		public RegisterNode(int register) { reg = register; }
		private int getRegister() { return reg; }
		void walkChildren(TreeWalker<?> walker) { }
	}
	
	public int reserveRegister() {
		localVariables++;
		return registerNumber++;
	}
	
	public Generator(CompilerTarget t) {
		if (!(t instanceof OptimizeTarget)) { /* FIXME */
			t = new OptimizeTarget(t);
		}
		
		localVariables = 1;
		registerNumber = 1; // R0 is for "self," initially.
		target = t;
	}
	
	public boolean isPreorderTraversal() {
		return false;
	}
	
	public void resetRegisters() {
		registerNumber = 1;
		localVariables = 1;
	}
	
	public RegisterNode processEntireMethod(String container, String methodName,
	                                        int arguments, Node n) {
		
		target.emitFunctionEnter(container, methodName, arguments, 0);
		
		currentName = methodName;
		currentContainer = container;
		currentArguments = arguments;
		
		RegisterNode result = super.processEntireMethod(container, methodName, arguments, n);
		
		target.emitReturn(reg(result));
		target.emitFunctionExit();
		
		return result;
	} 
	
	private int reg(Node n) {
		return ((RegisterNode)n).getRegister();
	}
	
	public void walk(StatementsNode n) {
		replace(new RegisterNode(reg(n.getLastChild())));
	}
	
	public void walk(AssignmentNode n) {
		int sourceRegister = reg(n.getRightHandSide());
		target.emitCopyInstruction(n.getRegisterNumber(), sourceRegister);
		
		replace(new RegisterNode(sourceRegister));
	}
	
	public void walk(LocalVariableNode n) {
		replace(new RegisterNode(n.getRegisterNumber()));
	}
	
	public void walk(IntegerNode n) {
		int number = reserveRegister();
		target.emitConstantLoadInstruction(number, n.getValue());
		replace(new RegisterNode(number));
	}
	
	public void walk(MethodCallNode n) {
		Node[] arguments = n.getArguments();
		int[] registers = new int[arguments.length];
		
		for (int i = 0; i < arguments.length; i++) {
			registers[i] = reg(arguments[i]);
		}
		
		int number = reserveRegister();
		
		target.emitCallInstruction(number, n.getMethodName(), reg(n.getSubject()), registers);
		
		replace(new RegisterNode(number));
	}
	
	public void walk(StateAssignmentNode n) {
		
	}
	
	public void walk(StateVariableNode n) {
		
	}
	
	public void walk(BlockNode n) {
		int closureNumber = nextClosureNumber++;
		String closureName = currentName + "-" + closureNumber;
		int num = registerNumber++;
		
		target.emitFunctionEnter(currentContainer, closureName, n.getNumberOfArguments() + localVariables, 0);
		
		n.actuallyWalkChildren(this);
		
		int register = reg(n.getChildNode());
		
		target.emitReturn(register);
		target.emitFunctionExit();
		target.emitLoadClosure(num, closureNumber);
		
		replace(new RegisterNode(num));
	}
	
	public void walk(ConstantNode n) {
		int number = reserveRegister();
		
		target.emitLoadConstantClass(number, n.getName());
		
		replace(new RegisterNode(number));
	}
	
	public void walk(StringNode n) {
		int number = reserveRegister();
		
		target.emitLoadConstantString(number, n.getValue());
		
		replace(new RegisterNode(number));
	}
	
	public void walk(NullNode n) {
		int number = reserveRegister();
		
		target.emitLoadNull(number);
		
		replace(new RegisterNode(number));
	}
}