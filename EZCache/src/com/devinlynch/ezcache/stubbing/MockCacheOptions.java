package com.devinlynch.ezcache.stubbing;

import com.devinlynch.ezcache.util.CacheableMethodHelper;
import com.devinlynch.ezcache.util.MethodAndArgs;

public class MockCacheOptions {
	
	/*
	 * Delete the object in cache if it exists
	 */
	public void delete() {
		MethodAndArgs lastCalledMethodAndArgs = MockCacheInterceptor.getLastCalledMethodAndArgs();
		if(lastCalledMethodAndArgs == null)
			return;
		CacheableMethodHelper helper = new CacheableMethodHelper(lastCalledMethodAndArgs.method, lastCalledMethodAndArgs.args);
		helper.delete();
	}
	
	/**
	 * Put the given value into the cache
	 * @param value
	 */
	public void put(Object value) {
		MethodAndArgs lastCalledMethodAndArgs = MockCacheInterceptor.getLastCalledMethodAndArgs();
		if(lastCalledMethodAndArgs == null)
			return;
		CacheableMethodHelper helper = new CacheableMethodHelper(lastCalledMethodAndArgs.method, lastCalledMethodAndArgs.args);
		helper.put(value);
	}
	
	/**
	 * Gets the value from cache if it exists
	 * @param value
	 * @return 
	 */
	public Object get() {
		MethodAndArgs lastCalledMethodAndArgs = MockCacheInterceptor.getLastCalledMethodAndArgs();
		if(lastCalledMethodAndArgs == null)
			return null;
		CacheableMethodHelper helper = new CacheableMethodHelper(lastCalledMethodAndArgs.method, lastCalledMethodAndArgs.args);
		return helper.get();
	}
}
