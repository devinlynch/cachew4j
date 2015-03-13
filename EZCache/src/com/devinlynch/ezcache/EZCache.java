package com.devinlynch.ezcache;

import com.devinlynch.ezcache.stubbing.CacheWrapper;
import com.devinlynch.ezcache.stubbing.MockCacheInterceptor;
import com.devinlynch.ezcache.stubbing.MockCacheOptions;

public class EZCache {
	
	/**
	 * Call this to wrap an object with a caching layer.  The first parameter is the object that you want to
	 * wrap. The second parameter is the class which contains the annotated descriptions 
	 * and which also implements the interface.  If the class of the object you are wrapping is exactly the
	 * same as the class that has your annotations, then use the other {@link EZCache#cache(Object)} 
	 * method.
	 * @param service
	 * @param interfaceClass
	 * @param describedClass
	 * @return
	 */
	public static<T> T cache(
			T service, 
			Class<?> describedClass) {
		return CacheWrapper.cache(service, describedClass);
	}
	
	/**
	 * Call this to wrap an object with a caching layer.  It is assumed that the class of the object passed will
	 * have the annotations required for caching.
	 * @param service
	 * @param interfaceClass
	 * @param describedClass
	 * @return
	 */
	public static<T> T cache(
			T service) {
		return CacheWrapper.cache(service);
	}
	
	/**
	 * Returns a mock object which can be used in conjunction with {@link EZCache#forKeyGeneratedBy(Object)} for
	 * manually interacting with the cache for a value generated by a method.
	 * @param o
	 * @return
	 */
	public static<T> T mock(T o) {
		return MockCacheInterceptor.mockCache(o);
	}
	
	/**
	 * After creating a mock object of the object you want to have values cached for, call this method with the argument
	 * being the return value of calling the mock object's method you want to define functionality for.
	 * <br />
	 * <br />
	 * For example: You can do ' forKeyGeneratedBy( mock( new MyService() ).get( "1234") ); '
	 * <br/>
	 * This will return you back {@link MockCacheOptions} for which you can perform on the cache for the key that would
	 * be generated by calling (new MyService()).get("1234").
	 * @param o
	 * @return
	 */
	public static MockCacheOptions forKeyGeneratedBy(Object o) {
		return new MockCacheOptions();
	}
}


