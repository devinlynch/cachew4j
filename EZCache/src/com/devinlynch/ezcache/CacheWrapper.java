package com.devinlynch.ezcache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.devinlynch.ezcache.util.CacheableHelper;

/**
 * Wraps any Object (usually a service) who has methods described with the {@link Cacheable}
 * annotation.  Call {@link CacheWrapper#wrapService(Object)} to decorate your object with
 * caching.
 * @author devinlynch
 *
 */
public class CacheWrapper implements InvocationHandler {
	private Object serviceToWrap;
	
	/**
	 * Call this to wrap any object for caching.  Invoking the returned object will cause
	 * for caching on invocation iff the method is described with the {@link Cacheable} 
	 * annotation.
	 * @param service
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static<T> T wrapObject(T service) {
		CacheWrapper wrapper = new CacheWrapper(service);
		return (T) Proxy.newProxyInstance(service.getClass().getClassLoader(), new Class[]{service.getClass()}, wrapper);
	}
	
	protected CacheWrapper(Object serviceToWrap) {
		this.serviceToWrap = serviceToWrap;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		
		// Check to see if the result is held in cache, if so return it
		CacheableHelper helper = new CacheableHelper(method, args);
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
