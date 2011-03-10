package chaldea.parser;

import chaldea.CompilerTarget;
import chaldea.processor.Generator;
import chaldea.target.DebugTarget;
import chaldea.target.OptimizeTarget;
import chaldea.target.BranchTarget;
import chaldea.target.LinkerTarget;

import org.yuanheng.cookcc.*;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.Stack;

@CookCCOption
public class ChaldeaParser extends Parser {
	private CompilerTarget target;
	private Map<String, Integer> localVariables;
	private String methodContainer;
	private String methodName;
	private String lineContext;
	private int lineNumber;
	private int closureNumber;
	private int registerNumber;
	private TreeSet<Integer> freedRegisters;
	private Stack<MethodState> stateStack;
	private Stack<ContextState> contextStack;
	private HashSet<String> loadedFiles;
	
	private class ContextState {
		int line;
		String name;
		
		public ContextState() {
			line = lineNumber;
			name = lineContext;
		}
		
		public void restore() {
			lineNumber = line;
			lineContext = name;
		}
	}
	
	private class MethodState {
		TreeSet<Integer> freed;
		int regNum;
		
		public MethodState() {
			freed = freedRegisters;
			
			freedRegisters = new TreeSet<Integer>();
			regNum = registerNumber;
		}
		
		public void restore() {
			freedRegisters = freed;
			registerNumber = regNum;
		}
	}
	
	public ChaldeaParser(CompilerTarget t, InputStream input, String context) {
		if (!(t instanceof OptimizeTarget)) {
			t = new OptimizeTarget(t);
		}
		
		target = t;
		lineContext = context;
		lineNumber = 1;
		contextStack = new Stack<ContextState>();
		loadedFiles = new HashSet<String>();
		loadedFiles.add(context);
		
		resetMethodState();
		
		setInput(input);
	}
	
	private void resetMethodState() {
		localVariables = new HashMap<String, Integer>();
		closureNumber = 0;
		freedRegisters = new TreeSet<Integer>();
		registerNumber = 1;
		stateStack = new Stack<MethodState>();
	}
	
	public ChaldeaParser(CompilerTarget t, File f) throws IOException {
		this(t, new FileInputStream(f), f.toString());
	}
	
	@CookCCToken
	static enum Token
	{
		@TokenGroup
		ATOM, INTEGER, NEWLINE, UNKNOWN, SEMICOLON, STRING, IVAR, CVAR,
		
		@TokenGroup
		OBRACKET, CBRACKET,
		
		@TokenGroup
		OPAREN, CPAREN, COMMA, ASSIGN,
		
		@TokenGroup
		PLUS, MINUS
	}
	
	protected boolean yyWrap() {
		if (yyInputStackSize() > 0) {
			yyPopInput();
			contextStack.pop().restore();
			return false;
		}
		
		return true;
	}
	
	protected void syntaxError(String message) {
		throw new SyntaxError(message, lineContext, lineNumber);
	}
	
	protected int allocateRegister() {
		if (freedRegisters.isEmpty()) return registerNumber++;
		else {
			int lowest = freedRegisters.first();
			freedRegisters.remove(lowest);
			
			return lowest;
		}
	}
	
	protected void freeRegister(int register) {
		if (localVariables.containsValue(register)) {
			/* do not free local variables */
		} else if (register == 0) {
			/* do not reuse self... we may need it later! */
		} else {
			freedRegisters.add(register);
		}
	}
	
	protected int symbolizeLocalVariable(String name) {
		if (name.equals("self")) {
			return 0;
		} else if (Character.isUpperCase(name.charAt(0))) {
			syntaxError("cannot have CamelCased local variable names.");
			return -1;
		} else if (name.equals("null")) {
			syntaxError("cannot declar a local variable 'null'");
			return -1;
		} else if (localVariables.containsKey(name)) {
			return localVariables.get(name);
		} else {
			int registerNumber = allocateRegister();
			localVariables.put(name, registerNumber);
			
			return registerNumber;
		}
	}
	
	public boolean yyParseError(int terminal) {
		System.err.println("TERM: " + terminal);
		
		return false;
	}
	
	/* Lexer rules */
	@Lex(pattern="[a-zA-Z_][_0-9a-zA-Z]*", token="ATOM") String lexA() { return yyText(); }
	@Lex(pattern="@[a-zA-Z][0-9a-zA-Z]*", token="IVAR")  String lexB() { return yyText(); }
	@Lex(pattern="@@[a-zA-Z][0-9a-zA-Z]*", token="CVAR") String lexC() { return yyText(); }
	@Lex(pattern="-?[0-9][0-9_]*", token="INTEGER")     Integer lexI() { return Integer.valueOf(yyText()); }
	@Lex(pattern="#.*")                     void lexD() { }
	@Lex(pattern="\\\"")                    void lexSS() { begin("STRING_STATE"); }
	@Lex(pattern="[ \t]+")                  void lexW() { }
	
	@Lex(pattern="[^\"]+\\\"", state="STRING_STATE", token="STRING")
		String lexSC() { begin("INITIAL"); return yyText().substring(0, yyLength() - 1); }
	
	
	@Lexs(patterns = {
		@Lex(pattern="[{]", token="OBRACKET"),
		@Lex(pattern="[}]", token="CBRACKET"),
		@Lex(pattern="\\+", token="PLUS"),
		@Lex(pattern="-", token="MINUS"),
		@Lex(pattern=";", token="SEMICOLON"),
		@Lex(pattern="[(]", token="OPAREN"),
		@Lex(pattern="[)]", token="CPAREN"),
		@Lex(pattern="[,]", token="COMMA"),
		@Lex(pattern="=", token="ASSIGN")
	}) Object lexE() { return null; }
	
	@Lex(pattern="\\n", token="NEWLINE") Object lexNewline() {
	  lineNumber += 1; target.emitSourceLine(lineContext, lineNumber); return null; }
	@Lex(pattern=".", token="UNKNOWN") Object lexOther() { return null; }
	
	
	/* Parser rules */
	
	@Rules(rules={
		@Rule(lhs="program", rhs="program require_stmt nl"),
		@Rule(lhs="program", rhs="program class_definition nl"),
		@Rule(lhs="program", rhs="nl"),
		@Rule(lhs="nl", rhs="nl NEWLINE"),
		@Rule(lhs="nl", rhs=""),
		@Rule(lhs="sep", rhs="sep NEWLINE"),
		@Rule(lhs="sep", rhs="sep SEMICOLON"),
		@Rule(lhs="sep", rhs="NEWLINE"),
		@Rule(lhs="sep", rhs="SEMICOLON"),
		@Rule(lhs="optsep", rhs="sep"),
		@Rule(lhs="optsep", rhs=""),
		@Rule(lhs="class_definition", rhs="class_name OBRACKET class_block CBRACKET"),
		@Rule(lhs="class_block", rhs="class_block method_declaration nl"),
		@Rule(lhs="class_block", rhs="nl")
	}) void parseA() { }
	
	@Rule(lhs="require_stmt", rhs="ATOM STRING", args="1 2")
	void parseRequire(String req, String filename) {
		if (loadedFiles.contains(filename)) {
			return;
		}
		
		loadedFiles.add(filename);
		
		try {
			yyPushInput(new FileInputStream(filename + ".chal"));
		} catch (java.io.FileNotFoundException e) {
			syntaxError("unable to open \"" + filename + "\"");
			return;
		}
		
		contextStack.add(new ContextState());
		lineContext = filename + ".chal";
		lineNumber = 1;
	}
	
	@Rule(lhs="class_name", rhs="ATOM ATOM", args="1 2")
		void parseB(String t, String n) { methodContainer = n; }
	
	@Rule(lhs="pm", rhs="PLUS")     Boolean parseC() { return Boolean.TRUE; }
	@Rule(lhs="pm", rhs="MINUS")    Boolean parseD() { return Boolean.FALSE; }
	
	/* <FIXME> */
	@Rules(rules={
		@Rule(lhs="args_defn", rhs="args_decl", args="1"),
		@Rule(lhs="args_defn", rhs="OPAREN args_decl CPAREN", args="2")
	})
	List<String> parseArgumentsDefinition(List<String> args) { return args; }
	
	@Rules(rules={
		@Rule(lhs="args_defn", rhs=""),
		@Rule(lhs="args_defn", rhs="OPAREN CPAREN")
	})
	List<String> parseEmptyArgumentsDefinition() { return new LinkedList<String>(); }
	
	@Rule(lhs="args_decl", rhs="ATOM", args="1")
	List<String> parseSingleArgumentDefinition(String arg) {
		List<String> args = new LinkedList<String>();
		args.add(arg);
		symbolizeLocalVariable(arg);
		
		return args;
	}
	
	@Rule(lhs="args_decl", rhs="args_decl COMMA ATOM", args="1 3")
	List<String> parseArgumentDefinition(List<String> arguments, String argument) {
		arguments.add(argument);
		
		return arguments;
	}
	/* </FIXME> */
	
	@Rule(lhs="method_header", rhs="pm ATOM args_defn", args="1 2 3")
	void parseMethodHeader(Boolean isPlus, String name, List<String> args) {	
		resetMethodState();
		
		methodName = (isPlus ? '.' : '#') + name; /* changes state */
		
		for (String argument : args) {
			symbolizeLocalVariable(argument);
		}
		
		target.emitFunctionEnter(methodContainer, methodName, args.size(), 0);
	}
	
	@Rule(lhs="method_declaration", rhs="method_header OBRACKET stmts1 CBRACKET", args="3")
	void parseMDwithBody(Integer reg) {
		target.emitReturn(reg);
		target.emitFunctionExit();
	}
	
	@Rule(lhs="method_declaration", rhs="method_header")
	void parseMD() {
		target.emitLoadNull(1);
		target.emitReturn(1);
		target.emitFunctionExit();
	}
	
	@Rule(lhs="stmts1", rhs="optsep stmts2 optsep", args="2")
	Integer parseST1a(Integer a) { return a; }
	
	@Rule(lhs="stmts1", rhs="optsep")
	Integer parseST1b() {
		int reg = allocateRegister();
		target.emitLoadNull(reg);
		return reg;
	}
	
	@Rule(lhs="stmts2", rhs="expr", args="1")
	Integer parseST2a(Integer reg) { return reg; }
	
	@Rule(lhs="stmts2", rhs="stmts2 sep expr", args="1 3")
	Integer parseST2b(Integer accum, Integer reg) {
		freeRegister(accum);
		return reg;
	}
	
	@Rule(lhs="expr", rhs="STRING", args="1")
	Integer parseString(String value) {
		Integer reg = allocateRegister();
		target.emitLoadConstantString(reg, value);
		return reg;
	}
	
	@Rule(lhs="expr", rhs="INTEGER", args="1")
	Integer parseInteger(Integer value) {
		int reg = allocateRegister();
		target.emitConstantLoadInstruction(reg, value);
		return reg;
	}
	
	@Rules(rules={
		@Rule(lhs="expr", rhs="ATOM args", args="1 2"),
		@Rule(lhs="expr", rhs="ATOM OPAREN args CPAREN", args="1 3")
	})
	Integer parseMethodCall(String atom, List<Integer> arguments) {
		int callee = arguments.remove(0);
		freeRegister(callee);
		
		int[] intified = new int[arguments.size()];
		for (int i = 0; i < intified.length; i++) {
			intified[i] = arguments.get(i);
			freeRegister(intified[i]);
		}
		
		int reg = allocateRegister();
		
		target.emitCallInstruction(reg, atom, callee, intified);
		
		return reg;
	}
	
	@Rule(lhs="expr", rhs="ATOM ASSIGN expr", args="1 3")
	Integer parseAssignment(String name, Integer src) {
		freeRegister(src);
		
		int dst = symbolizeLocalVariable(name);
		target.emitCopyInstruction(dst, src);
		return dst;
	}
	
	@Rule(lhs="expr", rhs="ATOM", args="1")
	Integer parseLocalVariable(String name) {
		if (name.equals("null")) {
			int reg = allocateRegister();
			target.emitLoadNull(reg);
			return reg;
		} else if (Character.isUpperCase(name.charAt(0))) {
			int reg = allocateRegister();
			target.emitLoadConstantClass(reg, name);
			return reg;
		} else {
			return symbolizeLocalVariable(name);
		}
	}
	
	@Rule(lhs="expr", rhs="IVAR ASSIGN expr", args="1 3")
	Integer parseStateAssignment(String variableName, Integer rightHandSide) {
		target.emitWriteStateVariable(0, variableName, rightHandSide);
		
		return rightHandSide;
	}
	
	@Rule(lhs="expr", rhs="IVAR", args="1")
	Integer parseInstanceVariable(String variableName) {
		int reg = allocateRegister();
		
		target.emitStateVariable(reg, 0, variableName);
		
		return reg;
	}
	
	@Rules(rules={
		@Rule(lhs="aexpr", rhs="expr", args="1"),
		@Rule(lhs="ablock", rhs="block", args="1")
	})
	List<Integer> parseSingleArgument(Integer argument) {
		List<Integer> list = new ArrayList<Integer>();
		list.add(argument);
		
		return list;
	}
	
	@Rules(rules={
		@Rule(lhs="aexpr", rhs="aexpr COMMA expr", args="1 3"),
		@Rule(lhs="aexpr", rhs="ablock expr", args="1 2"),
		@Rule(lhs="ablock", rhs="aexpr block", args="1 2"),
		@Rule(lhs="ablock", rhs="ablock block", args="1 2")
	})
	List<Integer> parseA(List<Integer> nodes, Integer toAdd) {
		nodes.add(toAdd);
		return nodes;
	}
	
	@Rules(rules={
		@Rule(lhs="args", rhs="aexpr", args="1"),
		@Rule(lhs="args", rhs="ablock", args="1")
	})
	List<Integer> parseAN(List<Integer> arg) { return arg; }
	
	@Rule(lhs="block0", rhs="OBRACKET")
	Integer parseBKS() {
		int closure = closureNumber++;
		int reg = allocateRegister();
		
		target.emitLoadClosure(reg, closure);
		target.emitFunctionEnter(methodContainer, methodName + '-' + closure, registerNumber /* <- FIXME */, 0);
		
		stateStack.push(new MethodState());
		
		return reg;
	}
	
	@Rule(lhs="block", rhs="block0 stmts1 CBRACKET", args="1 2")
	Integer parseBK(Integer reg, Integer ret) {
		target.emitReturn(ret);
		target.emitFunctionExit();
		
		stateStack.pop().restore();
		
		return reg;
	}
	
	public void parse() throws IOException {
		int result = yyParse();
		
		if (result != 0) {
			syntaxError("syntax error");
		}
	}
}