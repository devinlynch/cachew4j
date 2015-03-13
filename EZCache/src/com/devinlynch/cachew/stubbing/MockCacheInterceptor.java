package com.devinlynch.cachew.stubbing;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.devinlynch.cachew.annotations.CacheReturnValue;
import com.devinlynch.cachew.util.MethodAndArgs;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Intercepts method calls to a mocked object whose values should be cached.  This is for defining
 * custom values of the cache.
 * @author devinlynch
 *
 */
public class MockCacheInterceptor implements MethodInterceptor {
	private static Map<Long, MethodAndArgs> lastCalledPerThread = new HashMap<Long, MethodAndArgs>();
	
	public MockCacheInterceptor() {
	}
	
	@Override
	public Object intercept(Object object, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		if(method.getAnnotation(CacheReturnValue.class) == null) {
			throw new RuntimeException("The method ["+method.getName()+"] must be described with the @CacheReturnValue annotation.");
		}
		
		long id = Thread.currentThread().getId();
		lastCalledPerThread.put(new Long(id), new MethodAndArgs(method, args));
		
		return null;
	}
	
	public static MethodAndArgs getLastCalledMethodAndArgs() {
		long id = Thread.currentThread().getId();
		return lastCalledPerThread.get(new Long(id));
	}
	
	@SuppressWarnings("unchecked")
	public static<T> T mockCache(
			T object) {
		MockCacheInterceptor interceptor = new MockCacheInterceptor();
		
		Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(object.getClass());
        enhancer.setCallback(interceptor);
		
		return (T) enhancer.create();
	}
		
}
