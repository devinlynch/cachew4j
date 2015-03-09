package com.devinlynch.ezcache.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.ObjectExistsException;

import com.devinlynch.ezcache.CacheKeyCompliant;
import com.devinlynch.ezcache.CacheReturnValue;
import com.devinlynch.ezcache.Cacheable;
import com.devinlynch.ezcache.EZCache;
import com.devinlynch.ezcache.EZCacheKey;

/**
 * A helper class which can put or get objects from a cache if a method is described with
 * a {@link CacheReturnValue} annotation.
 * @author devinlynch
 *
 */
public class CacheableMethodHelper {
	private Method method;
	private Object[] args;
	
	public CacheableMethodHelper(Method m, Object[] args) {
		this.method = m;
		this.args = args;
	}
	
	/**
	 * Puts the given object into the cache iff the {@link CacheableMethodHelper#method} is described
	 * with a {@link CacheReturnValue} annotation and if the object is {@link Cacheable}.
	 * @param o
	 */
	public void put(Object o) {
		// First check to see if the method is cache-compliant
		CacheReturnValue annotation = getAnnotation();
		if(annotation == null || o == null)
			return;
		
		if(!(o instanceof Cacheable)) {
			throw new RuntimeException("The return type of every method described with CacheReturnValue must implement Cacheable");
		}
		
		Cacheable cacheable = (Cacheable)o;
		try {
			EZCache ezCache = new EZCache();
			ezCache.put(cacheable);
			mapMethodKeyToObject(cacheable);
		} catch (Exception e) {
			System.out.println("Error putting into cache");
			e.printStackTrace();
			return;
		}
	}
	
	
	public Object get() {
		// First check if the method is cache-compliant
		CacheReturnValue annotation = getAnnotation();
		if(annotation == null)
			return null;
		if(!Cacheable.class.isAssignableFrom(method.getReturnType())) {
			throw new RuntimeException("The return type of every method described with CacheReturnValue must implement Cacheable");
		}
		try {
			EZCache ezCache = new EZCache();
			CacheableMapping mappingToObject = getMappingToObject();
			if(mappingToObject == null)
				return null;
			@SuppressWarnings({ "rawtypes", "unchecked" })
			Object object = ezCache.get(new EZCacheKey(mappingToObject.getMappedObjectId(), mappingToObject.getMappedObjectClass()));
			return object;
		} catch (Exception e) {
			System.out.println("Error getting from cache");
			e.printStackTrace();
			return null;
		}
	}
	
	
	protected CacheReturnValue getAnnotation() {
		return method.getAnnotation(CacheReturnValue.class);
	}
	
	public void mapMethodKeyToObject(Cacheable obj) {
		CacheableMapping mapping = new CacheableMapping(obj, getMappingKey());
		EZCache ez = new EZCache();
		ez.put(mapping);
	}
	
	public CacheableMapping getMappingToObject() {
		EZCache ez = new EZCache();
		CacheableMapping mapping = (CacheableMapping) ez.get(new EZCacheKey<CacheableMapping>(getMappingKey(), CacheableMapping.class));
		return mapping;
	}
	
	
	protected String getMappingKey() {
		String ps = "";
		for(Class<?> c : method.getParameterTypes()) {
			ps += c.getName();
		}
		String a = "";
		for(Object o: args) {
			String val = null;
			if(o != null) {
				val = o.toString();
				if(o instanceof CacheKeyCompliant) {
					val = ((CacheKeyCompliant)o).getCacheKey();
				}
			}
			a+= val + ",";
		}
		return "EZCache[METHODMAPPING:methodHashCode=[hc=["+method.hashCode()+"]numP=["+method.getParameterTypes().length+"]ps=["+ps+"]args=["+a+"]]]";	
	}
	

}
