package chaldea.runtime;

public class NullType extends AbstractType {
	private ChaldeaValue nullInstance;
	
	public NullType(ObjectType parentType) {
		super(parentType);
		
		final Type nullType = this;
		
		nullInstance = new AbstractValue() {
			public Type getType() {
				return nullType;
			}
			
			public String toString() {
				return "<null>";
			}
		};
	}
	
	public String getName() {
		return "NullType";
	}
	
	public ChaldeaValue getInstance() {
		return nullInstance;
	}
	
	public int getSizeOfInstance() {
		return 0;
	}
}