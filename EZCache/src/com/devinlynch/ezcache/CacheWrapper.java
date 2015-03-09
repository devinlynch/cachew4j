package com.devinlynch.ezcache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.devinlynch.ezcache.util.CacheableMethodHelper;

/**
 * Wraps any Object (usually a service) who has methods described with the {@link CacheReturnValue}
 * annotation.  Call {@link CacheWrapper#wrapService(Object)} to decorate your object with
 * caching.
 * @author devinlynch
 *
 */
public class CacheWrapper implements InvocationHandler {
	private Object serviceToWrap;
	private Class<?> describedClass;
	
		
	/**
	 * Call this to wrap an object with a caching layer.  The first parameter is the object that you want to
	 * wrap.  The second parameter is the interface the the wrapped object implements.  This returning object
	 * will be of this interface.  The third parameter is the class which contains the annotated descriptions 
	 * and which also implements the interface.  In many cases, this class will be the same as the interface 
	 * class or the class of the object being wrapped.  It is there for convenience in case your annotations
	 * are placed on another implementation of the interface.
	 * @param service
	 * @param interfaceClass
	 * @param describedClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static<INTERFACE,IMPL extends INTERFACE,DESCRIBED_CLASS extends INTERFACE> INTERFACE wrapObject(
			IMPL service, 
			Class<INTERFACE> interfaceClass, 
			Class<DESCRIBED_CLASS> describedClass) {
		CacheWrapper wrapper = new CacheWrapper(service, describedClass);
		return (INTERFACE) Proxy.newProxyInstance(service.getClass().getClassLoader(), new Class[]{interfaceClass}, wrapper);
	}
	
	/**
	 * Call this to wrap an object with a caching layer.  The first parameter is the object that you want to
	 * wrap.  The second parameter is the interface the the wrapped object implements.  This returning object
	 * will be of this interface.  Using this method assumes that caching annotations are placed on the interfaces'
	 * methods.
	 * @param service
	 * @param interfaceClass
	 * @param describedClass
	 * @return
	 */
	public static<INTERFACE,IMPL extends INTERFACE,DESCRIBED_CLASS extends INTERFACE> INTERFACE wrapObject(
			IMPL service, 
			Class<INTERFACE> interfaceClass) {
		return  wrapObject(service, interfaceClass, interfaceClass);
	}
	
	protected CacheWrapper(Object serviceToWrap, Class<?> describedClass) {
		this.serviceToWrap = serviceToWrap;
		this.describedClass = describedClass;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		
		// Check to see if the result is held in cache, if so return it
		CacheableMethodHelper helper = new CacheableMethodHelper(describedClass.getMethod(method.getName(), method.getParameterTypes()), args);
		Object cachedObject = helper.get();
		if(cachedObject != null) {
			System.out.println("Read from cache");
			return cachedObject;
		}
		
		Object result = method.invoke(serviceToWrap, args);
		// But the result in cache if its not null
		if(result != null) {
			System.out.println("Put into cache");
			helper.put(result); 
		}
		return result;
	}
}
