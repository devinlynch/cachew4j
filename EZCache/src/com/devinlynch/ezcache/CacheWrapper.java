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
	
	/**
	 * Call this to wrap any object for caching.  Invoking the returned object will cause
	 * for caching on invocation iff the method is described with the {@link CacheReturnValue} 
	 * annotation.
	 * @param service
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static<T> T wrapObject(T service, Class<?> interfaceClass) {
		CacheWrapper wrapper = new CacheWrapper(service);
		return (T) Proxy.newProxyInstance(service.getClass().getClassLoader(), new Class[]{interfaceClass}, wrapper);
	}
	
	protected CacheWrapper(Object serviceToWrap) {
		this.serviceToWrap = serviceToWrap;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		
		// Check to see if the result is held in cache, if so return it
		CacheableMethodHelper helper = new CacheableMethodHelper(serviceToWrap.getClass().getMethod(method.getName(), method.getParameterTypes()), args);
		Object cachedObject = helper.get();
		if(cachedObject != null) {
			return cachedObject;
		}
		
		Object result = method.invoke(serviceToWrap, args);
		// But the result in cache if its not null
		if(result != null) {
			helper.put(result); 
		}
		return result;
	}
}
