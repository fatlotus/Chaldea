package chaldea.runtime;

public class StringType extends AbstractType {
	public StringType(ObjectType superType) {
		super(superType);
		
		addMethod(new NativeMethod() {
			public String getName() { return "#concat"; }
			public int getNumberOfArguments() { return 1; }
			public ChaldeaValue methodBody(TypeSpace ts, ChaldeaValue callee, ChaldeaValue[] args) {
				return ts.wrap(callee.toString() + args[0].toString());
			}
		});
		
		addMethod(new NativeMethod() {
			public String getName() { return "#eq"; }
			public int getNumberOfArguments() { return 1; }
			public ChaldeaValue methodBody(TypeSpace ts, ChaldeaValue callee, ChaldeaValue[] args) {
				return ts.wrap(callee.toString().equals(args[0].toString()));
			}
		});
	}
	
	public String getName() {
		return "String";
	}
	
	public int getSizeOfInstance() {
		return -1;
	}
}