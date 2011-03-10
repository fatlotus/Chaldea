package chaldea.runtime;

import chaldea.CompilerTarget;
import chaldea.target.EmulatorTarget;

public abstract class AbstractMethod implements Method {
	public abstract String getDeclaredContainer();
	public abstract String getName();
	public abstract int getNumberOfRegisters();
	public abstract int getNumberOfArguments();
	protected abstract void methodBody(CompilerTarget target);
	public boolean isNative() { return false; }
	
	public void evaluateInto(CompilerTarget target) {
		target.emitFunctionEnter(getDeclaredContainer(), getName(), getNumberOfArguments(), getNumberOfRegisters());
		
		methodBody(target);
		
		target.emitFunctionExit();
	}
	
	public ChaldeaValue evaluate(TypeSpace ts, ChaldeaValue callee, ChaldeaValue[] arguments) {
		EmulatorTarget emu = new EmulatorTarget(ts);
		
		emu.emitFunctionEnter(getDeclaredContainer(), getName(), getNumberOfArguments(), getNumberOfRegisters());
		emu.setRegister(0, callee);
		
		int i;
		
		for (i = 0; i < arguments.length; i++) {
			emu.setRegister(i + 1, arguments[i]);
		}
		
		for (; i < getNumberOfArguments(); i++) {
			emu.setRegister(i + 1, ts.getNullValue());
		}
		
		try {
			methodBody(emu);
			throw new RuntimeException("something has gone seriously wrong!");
		} catch (ReturnException e) {
			ChaldeaValue returnValue = e.getReturnValue();
			
			emu.emitFunctionExit();
			
			for (i = 0; i < arguments.length; i++) { /* FIXME */
				arguments[i] = emu.getRegister(i + 1);
			}
			
			return returnValue;
		}
	}
}