package chaldea.runtime;

public class BufferType extends AbstractType {
	public BufferType(ObjectType superType) {
		super(superType);
	}
	
	public String getName() {
		return "Buffer";
	}
	
	public int getSizeOfInstance() {
		return -1;
	}
}