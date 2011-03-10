package chaldea.runtime;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

import chaldea.runtime.anno.*;

public class JavaWrapperType extends AbstractType {
	private String name;
	
	private abstract class WrappedValue extends AbstractValue {
		public abstract Object getWrappedValue();
		public abstract Type getType();
	}
	
	public JavaWrapperType(TypeSpace typeSpace, final Class<?> klass) {
		super(typeSpace.getObjectType());
		
		ChaldeaType t = klass.getAnnotation(ChaldeaType.class);
		
		if (t == null) {
			throw new IllegalArgumentException("class " + klass + " has no ChaldeaType annotation.");
		}
		
		name = t.value();
		
		final Type parentType = this; 
		
		for (java.lang.reflect.Method method : klass.getMethods()) {
			chaldea.runtime.anno.Method decl = method.getAnnotation(chaldea.runtime.anno.Method.class);
			
			if (decl != null) {
				final boolean isStatic = Modifier.isStatic(method.getModifiers());
				
				final String methodName = (isStatic ? "." : "#") + decl.value();
				final Class<?>[] types = method.getParameterTypes();
				
				final boolean returnsVoid = method.getReturnType().equals(void.class);
				final boolean firstIsTypeSpace = types.length > 1 && types[0].equals(TypeSpace.class);
				
				final int numArguments = types.length - (firstIsTypeSpace ? 1 : 0); 
				
				final java.lang.reflect.Method wrappedMethod = method;
				
				addMethod(new NativeMethod() {
					public String getName() {
						return methodName;
					}
					
					public int getNumberOfArguments() {
						return numArguments;
					}
					
					public ChaldeaValue methodBody(TypeSpace ts, ChaldeaValue chaldeaCallee, ChaldeaValue[] args) {
						Object callee = isStatic ? null : ((WrappedValue)chaldeaCallee).getWrappedValue();
						
						Object[] javaifiedArgs = args;
						
						if (firstIsTypeSpace) {
							Object[] newArgs = new Object[javaifiedArgs.length + 1];
							newArgs[0] = ts;
							
							for (int i = 0; i < javaifiedArgs.length; i++) {
								newArgs[i + 1] = javaifiedArgs[i];
							}
							
							javaifiedArgs = newArgs;
						}
						
						Object returnValue;
						
						try {
							returnValue = wrappedMethod.invoke(callee, javaifiedArgs);
						} catch (Throwable t) {
							throw new RuntimeException(t);
						}
						
						if (returnsVoid) {
							return ts.getNullValue();
						} else {
							return (ChaldeaValue)returnValue;
						}
					}
				});
			}
		}
		
		
		addMethod(new NativeMethod() {
			public String getName() {
				return ".new";
			}
			
			public int getNumberOfArguments() {
				return -1;
			}
			
			public ChaldeaValue methodBody(TypeSpace ts, ChaldeaValue callee, ChaldeaValue[] args) {
				final Object instance;
				
				try {
					instance = klass.newInstance();
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
				
				return new WrappedValue() {
					public Object getWrappedValue() {
						return instance;
					}
					
					public Type getType() {
						return parentType;
					}
				};
			}
		});
	}
	
	public int getSizeOfInstance() {
		return 0;
	}
	
	public String getName() {
		return name;
	}
}