package com.devinlynch.cachew.stubbing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.devinlynch.cachew.annotations.CacheReturnValue;
import com.devinlynch.cachew.util.CacheableMethodHelper;

/**
 * Wraps any Object (usually a service) who has methods described with the {@link CacheReturnValue}
 * annotation.  Call {@link CacheWrapper#wrapService(Object)} to decorate your object with
 * caching.
 * @author devinlynch
 *
 */
public class CacheWrapper implements MethodInterceptor {
	private Object serviceToWrap;
	private Class<?> describedClass;
	

	@SuppressWarnings("unchecked")
	public static<T> T cache(
			T service, 
			Class<T> clazz,
			Class<?> describedClass) {
		CacheWrapper wrapper = new CacheWrapper(service, describedClass);
		
		Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(wrapper);
		
		return (T) enhancer.create();
		
	}
	
	@SuppressWarnings("unchecked")
	public static<T, V extends T> T cache(
			T service) {
		
		CacheWrapper wrapper = new CacheWrapper(service, service.getClass());
		
		Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(service.getClass());
        enhancer.setCallback(wrapper);
		
		return (T) enhancer.create();
		
	}
	

	
	protected CacheWrapper(Object serviceToWrap, Class<?> describedClass) {
		this.serviceToWrap = serviceToWrap;
		this.describedClass = describedClass;
	}

	@Override
	public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy)
			throws Throwable {
		if(describedClass.getMethod(method.getName(), method.getParameterTypes()).getAnnotation(CacheReturnValue.class) == null) {
			try {
				return method.invoke(serviceToWrap, args);
			} catch(InvocationTargetException e) {
				throw e.getCause();
			}
		}
		
		Method describedMethod = describedClass.getMethod(method.getName(), method.getParameterTypes());
		if(describedMethod == null) {
			throw new RuntimeException("The class you initialize for caching MUST have the same methods as the class you specified as having the annotations");
		}
		
		// Check to see if the result is held in cache, if so return it
		CacheableMethodHelper helper = new CacheableMethodHelper(describedMethod, args);
		Object cachedObject = helper.get();
		if(cachedObject != null) {
			//System.out.println("Got from cache for: "+method.getName());
			return cachedObject;
		}
		
		Object result = null;
		try {
			result = method.invoke(serviceToWrap, args);
		} catch(InvocationTargetException e) {
			throw e.getCause();
		}
		
		
		// But the result in cache if its not null
		if(result != null) {
			//System.out.println("Put in cache for: "+method.getName());
			helper.put(result); 
		}
		return result;
	}
}
