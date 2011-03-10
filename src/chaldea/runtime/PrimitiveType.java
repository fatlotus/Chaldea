package chaldea.runtime;

public class PrimitiveType extends AbstractType {
	public PrimitiveType(ObjectType superType) {
		super(superType);
		
		addMethod(new NativeMethod() {
			public String getName() { return "#add"; }
			public int getNumberOfArguments() { return 1; }
			public ChaldeaValue methodBody(TypeSpace ts, ChaldeaValue callee, ChaldeaValue[] args) {
				IntegerValue other = (IntegerValue)(args[0]);
				IntegerValue self = (IntegerValue)callee;
				
				return ts.wrap(other.getValue() + self.getValue());
			}
		});
		
		addMethod(new NativeMethod() {
			public String getName() { return "#gt"; }
			public int getNumberOfArguments() { return 1; }
			public ChaldeaValue methodBody(TypeSpace ts, ChaldeaValue callee, ChaldeaValue[] args) {
				IntegerValue other = (IntegerValue)(args[0]);
				IntegerValue self = (IntegerValue)callee;
				
				return ts.wrap(self.getValue() > other.getValue());
			}
		});
		
		addMethod(new NativeMethod() {
			public String getName() { return "#lt"; }
			public int getNumberOfArguments() { return 1; }
			public ChaldeaValue methodBody(TypeSpace ts, ChaldeaValue callee, ChaldeaValue[] args) {
				IntegerValue other = (IntegerValue)(args[0]);
				IntegerValue self = (IntegerValue)callee;
				
				return ts.wrap(self.getValue() < other.getValue());
			}
		});
		
		addMethod(new NativeMethod() {
			public String getName() { return "#eq"; }
			public int getNumberOfArguments() { return 1; }
			public ChaldeaValue methodBody(TypeSpace ts, ChaldeaValue callee, ChaldeaValue[] args) {
				if (!(args[0] instanceof IntegerValue)) {
					return ts.wrap(false);
				} else {
					IntegerValue other = (IntegerValue)(args[0]);
					IntegerValue self = (IntegerValue)callee;
				
					return ts.wrap(self.getValue() == other.getValue());
				}
			}
		});
	}
	
	public String getName() {
		return "Integer";
	}
	
	public int getSizeOfInstance() {
		return 4;
	}
}