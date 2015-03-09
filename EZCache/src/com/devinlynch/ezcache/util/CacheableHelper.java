package com.devinlynch.ezcache.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.devinlynch.ezcache.CacheReturnValue;

/**
 * A helper class which can put or get objects from a cache if a method is described with
 * a {@link CacheReturnValue} annotation.
 * @author devinlynch
 *
 */
public class CacheableHelper {
	private Method method;
	private Object[] args;
	
	public CacheableHelper(Method m, Object[] args) {
		this.method = m;
		this.args = args;
	}
	
	/**
	 * Puts the given object into the cache iff the {@link CacheableHelper#method} is described
	 * with a {@link CacheReturnValue} annotation.
	 * @param o
	 */
	public void put(Object o) {
		// First check to see if the method is cache-compliant
		CacheReturnValue cacheable = getCacheable();
		if(cacheable == null || o == null)
			return;
		Class<?> cacheClass = cacheable.cacheClass();
		try {
			Method cacheMethod;
			Object cacheInstance;
			
			// Create the parameters that will be called for the put method of the AbstractCache implementation
			List<Class<?>> params = new ArrayList<Class<?>>(Arrays.asList(method.getParameterTypes()));
			params.add(method.getReturnType());
			Class<?>[] arr = new Class<?>[params.size()];
			params.toArray(arr);
			
			// Get the defined put method for the cache class and then invoke it with the key and the value
			cacheMethod = cacheClass.getMethod(cacheable.putMethodName(), arr);
			cacheInstance = cacheClass.newInstance();
			List<Object> argsList = new ArrayList<Object>(Arrays.asList(args));
			argsList.add(o);
			cacheMethod.invoke(cacheInstance, argsList.toArray());
		} catch (NoSuchMethodException ee) {
			System.out.println("No method found in cacheClass: " + cacheClass);
			ee.printStackTrace();
		} catch (Exception e) {
			System.out.println("Error putting into cache");
			e.printStackTrace();
			return;
		}
	}
	
	public Object get() {
		// First check if the method is cache-compliant
		CacheReturnValue cacheable = getCacheable();
		if(cacheable == null)
			return null;
		try {
			// Get the get method of the AbstractCache implementation
			Class<?> cacheClass = cacheable.cacheClass();
			Method cacheMethod;
			Object cacheInstance;
			
			// Invoke the method and return the result
			cacheMethod = cacheClass.getMethod(cacheable.getMethodName(), method.getParameterTypes());
			cacheInstance = cacheClass.newInstance();
			return cacheMethod.invoke(cacheInstance, args);
		} catch (Exception e) {
			System.out.println("Error getting from cache");
			e.printStackTrace();
			return null;
		}
	}
	
	
	protected CacheReturnValue getCacheable() {
		return method.getAnnotation(CacheReturnValue.class);
	}
}
