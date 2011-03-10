package chaldea;

import chaldea.parser.ChaldeaParser;
import chaldea.parser.SyntaxError;
import chaldea.runtime.TypeSpace;
import chaldea.runtime.Type;
import chaldea.runtime.ReturnException;
import chaldea.target.OptimizeTarget;
import chaldea.target.LinkerTarget;
import chaldea.target.DebugTarget;
import chaldea.target.BranchTarget;
import chaldea.target.EmulatorTarget;
import chaldea.CompilerTarget;

import java.io.IOException;
import java.io.File;

public class Main {
	public static void main(String[] args) throws IOException {
		TypeSpace types = new TypeSpace();
		LinkerTarget linker = new LinkerTarget(types);
		CompilerTarget target = new BranchTarget(new DebugTarget(), linker);
		
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		

		ChaldeaParser parser = new ChaldeaParser(target, new File("test/unit.chal"));
		
		try {
			parser.parse();
		} catch (SyntaxError e) {
			System.err.println("syntax error:");
			System.err.println(e);
			return;
		}
		
		Type launcher = types.getTypeWithName("__launcher");
		
		CompilerTarget output = new EmulatorTarget(types);
		
		try {
			launcher.methodDispatch(".launch", output);
		} catch (ReturnException e) { }
		
		/*
		CompilerTarget target = new BranchTarget(new DebugTarget(), new EmulatorTarget());
		
		target.emitFunctionEnter("Kernel.initialize", 2);
		target.emitConstantLoadInstruction(0, 5);
		target.emitConstantLoadInstruction(1, 10);
		target.emitAddInstruction(0, 0, 1);
		target.emitCallInstruction("print", 0, new int[0]);
		target.emitFunctionExit();
		*/
	}
}