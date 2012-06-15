package org.kwince.contribs.osem.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kwince.contribs.osem.annotations.EventListener;
import org.kwince.contribs.osem.annotations.PostOsemCreate;
import org.kwince.contribs.osem.annotations.PostOsemDelete;
import org.kwince.contribs.osem.annotations.PostOsemRead;
import org.kwince.contribs.osem.annotations.PostOsemUpdate;
import org.kwince.contribs.osem.annotations.PreOsemCreate;
import org.kwince.contribs.osem.annotations.PreOsemDelete;
import org.kwince.contribs.osem.annotations.PreOsemRead;
import org.kwince.contribs.osem.annotations.PreOsemUpdate;

public class EventDispatcher {
	
	private EventListener meta;
	
	private static EventDispatcher INSTANCE = null;

    static public EventDispatcher getEventDispatcher() {
        if( INSTANCE == null ) {
            INSTANCE = new EventDispatcher();
        }
        return INSTANCE;
    }

    private EventDispatcher() {
    }
    
	private class HandlerInfo {
		public Object handler;
		public Method method;
	}
	
	@SuppressWarnings("unchecked")
	private final static Class<? extends Annotation>[] lifecycleAnnotations = new Class[]{
			PostOsemCreate.class, PostOsemRead.class, PostOsemUpdate.class, PostOsemDelete.class,
			PreOsemCreate.class, PreOsemRead.class, PreOsemUpdate.class, PreOsemDelete.class};
		
	private static Map<Class<?>, Set<HandlerInfo>> handlers = new HashMap<Class<?>, Set<HandlerInfo>>();
	
	private static Map<Class<?>, HashSet<Method>> methods = new HashMap<Class<?>, HashSet<Method>>();
	 
	public void register(Class<?> clazz) {
		
		this.meta = clazz.getAnnotation(EventListener.class);
	
		if (this.meta!=null) {
			System.out.println("The event listener is " + meta.value().getCanonicalName());
			
			Class<?> _handler = null;
			
			try {
				_handler = Class.forName(meta.value().getCanonicalName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			try {
				Object handler = _handler.newInstance();
				
				HashSet<Method> typeMethods = methods.get(clazz);
				if (typeMethods == null) {
					typeMethods = new HashSet<Method>();
			        methods.put(clazz, typeMethods);
			    }
				 
				for (Method method : handler.getClass().getMethods()) {
					for(Class<? extends Annotation> c : lifecycleAnnotations) {
						if (method.isAnnotationPresent(c) && !typeMethods.contains(method)) {
							addTypeSpecificHandler(handler, clazz, method);
							typeMethods.add(method);
						}
					}
				}
				
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
    	}
	}
	
	// For test purpose only
	public void addHandler(final Object handler) {
		
		for (Method method : handler.getClass().getMethods()) {
            if (method.getAnnotation(PostOsemCreate.class)!=null
            	|| method.getAnnotation(PreOsemCreate.class)!=null) {
            	Class<?>[] paramTypes = method.getParameterTypes();
            	System.out.println("paramTypes.length " + paramTypes.length);
                if (paramTypes.length == 1)
                {
                	System.out.println("Def Event " + paramTypes[0].getName());
                	System.out.println("method name " + method.getName());
                    addTypeSpecificHandler(handler, paramTypes[0], method);
                }
            }
        }
	}
	
	private void addTypeSpecificHandler(final Object handler, final Class<?> type, Method method) {
        Set<HandlerInfo> typeHandlers = handlers.get(type);
        
        if (typeHandlers == null) {
        	typeHandlers = new HashSet<HandlerInfo>();
            handlers.put(type, typeHandlers);
        }
        
        HandlerInfo info = new HandlerInfo();
        info.handler = handler;
        info.method = method;
        // Add the listener
        System.out.print("Add the listener: ");
        System.out.print("handler [" + handler.getClass().getName() + "] ");
        System.out.println("method [" + method.getName() + "] ");
        typeHandlers.add(info);
	}

	public void publish(Class<? extends Annotation> eventMethod, Class<?> clazz, Object entity) {
		Set<HandlerInfo> typeHandlers = handlers.get(clazz);
		if (typeHandlers==null) {
			return;
		}
		
		for(HandlerInfo info : typeHandlers) {
			Object handler = info.handler;
			
			if (handler!=null) {
				try {
					
					if (info.method.isAnnotationPresent(eventMethod)) {
						info.method.invoke(handler, entity);
					}
					
				} catch (IllegalArgumentException e) {					
					e.printStackTrace();
				} catch (IllegalAccessException e) {						
					e.printStackTrace();
				} catch (InvocationTargetException e) {				
					e.printStackTrace();
				}
			}
		}
	}
	
	public void publish(Class<? extends Annotation> eventMethod, Class<?> clazz) {
		publish(eventMethod, clazz, new Object());
	}
}