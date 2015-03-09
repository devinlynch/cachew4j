package com.devinlynch.ezcache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cacheable {
	/**
	 * The class which is to be used for getting and putting into the cache.  This should be a subclass
	 * of {@link AbstractCache}
	 * @return
	 */
	Class<? extends AbstractCache<?>> cacheClass();
	
	/**
	 * The Name of the method which is used to put into the cache
	 * @return
	 */
	String putMethodName() default "put";
	
	/**
	 * The Name of the method which is used to get from the cache
	 * @return
	 */
	String getMethodName() default "get";
}