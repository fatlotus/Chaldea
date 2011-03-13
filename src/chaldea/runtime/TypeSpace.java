package chaldea.runtime;

import java.util.Map;
import java.util.HashMap;

import chaldea.CompilerTarget;

public class TypeSpace {
	public class AddedType {
		private Type typeToAdd;
		private int staticOffset;
	}
	
	private PrimitiveType primitiveType;
	private ObjectType rootType;
	private BufferType bufferType;
	private StringType stringType;
	private MetaType metaType;
	private NullType nullType;
	private ClosureType closureType;
	private Map<String, Type> types;
	
	public TypeSpace() {
		rootType = new ObjectType();
		primitiveType = new PrimitiveType(rootType);
		bufferType = new BufferType(rootType);
		stringType = new StringType(rootType);
		nullType = new NullType(rootType);
		closureType = new ClosureType(rootType);
		metaType = new MetaType(rootType);
		
		types = new HashMap<String, Type>();
		
		addType(primitiveType);
		addType(rootType);
		addType(bufferType);
		addType(stringType);
		addType(nullType);
		addType(metaType);
		addType(closureType);
		addType(chaldea.parser.meta.MetaParser.class);
		addType(chaldea.api.Window.class);
	}
	
	public Type getObjectType() {
		return rootType;
	}
	
	public Type getClosureType() {
		return closureType;
	}
	
	public void addType(Type t) {
		if (types.containsKey(t.getName())) {
			throw new ChaldeaRuntimeError("duplicate class \"" + t.getName() + "\"");
		}
		
		types.put(t.getName(), t);
	}
	
	public void addType(Class<?> t) {
		addType(new JavaWrapperType(this, t));
	}
	
	public Type getTypeWithName(String name) {
		return types.get(name);
	}
	
	public ChaldeaValue wrap(final int value) {
		return new IntegerValue() {
			public String toString() {
				return Integer.toString(value);
			}
			
			public Type getType() {
				return primitiveType;
			}
			
			public int getValue() {
				return value;
			}
		};
	}
	
	public ChaldeaValue wrap(final String value) {
		return new AbstractValue() {
			public String toString() {
				return value;
			}
			
			public Type getType() {
				return stringType;
			}
			
			public String getValue() {
				return value;
			}
		};
	}
	
	public ChaldeaValue wrap(boolean value) { /* FIXME */
		return value ? wrap(1) : getNullValue();
	}
	
	public ChaldeaValue getNullValue() {
		return nullType.getInstance();
	}
	
	public void writeTypesInto(CompilerTarget target) {
		for (Type t : types.values()) {
			t.writeTo(target);
		}
	}
	
	public ReflectedValue reflectOn(String className) {
		final Type subject = getTypeWithName(className);
		
		return new ReflectedValue() {
			@Override
			public Type getType() {
				return metaType;
			}
			
			@Override
			public Type getRepresentingType() {
				return subject;
			}
			
			@Override
			public String toString() {
				return "<reflect " + getRepresentingType() + ">";
			}
		};
	}
}