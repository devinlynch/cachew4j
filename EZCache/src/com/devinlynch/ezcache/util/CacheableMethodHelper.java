package com.devinlynch.ezcache.util;

import java.io.Serializable;
import java.lang.reflect.Method;

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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void put(Object o) {
		// First check to see if the method is cache-compliant
		CacheReturnValue annotation = getAnnotation();
		if(annotation == null || o == null)
			return;
		
		Cacheable cacheable;
		if(o instanceof Cacheable) {
			// If the object is of type Cacheable, then we will be storing the object itself
			cacheable = (Cacheable)o;
		} else if(o instanceof Serializable) {
			// Otherwise if it is serializeable then we will store a SimpleCacheable with the value being the object
			cacheable = new SimpleCacheable((Serializable) o, getMappingKey(), annotation.timeToLiveSeconds(), annotation.timeToIdleSeconds());
		} else {
			throw new RuntimeException("The method ["+method.toGenericString()+"] must have a serializeable return type to be cacheable");
		}
		
		try {
			EZCache ezCache = new EZCache();
			ezCache.put(cacheable);
			if(o instanceof Cacheable) {
				// If the object is of type Cacheable, we need to store a mapping of this method to the key of the object
				mapMethodKeyToObject(cacheable);
			}
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
		try {
			EZCache ezCache = new EZCache();
			CacheableMapping mappingToObject = getMappingToObject();
			if(mappingToObject != null) {
				// First check to see if we have a mapping stored from the method to a cached object.  If so, we can return
				// the mapped object
				@SuppressWarnings({ "rawtypes", "unchecked" })
				Object object = ezCache.get(new EZCacheKey(mappingToObject.getMappedObjectId(), mappingToObject.getMappedObjectClass()));
				return object;
			} else {
				// Now check to see if there is a SimpleCacheable stored and if so we can return that
				SimpleCacheable<?> simpleCacheable = getSimpleCacheable();
				if(simpleCacheable != null) {
					return simpleCacheable.getValue();
				}
			}
			return null;
		} catch (Exception e) {
			System.out.println("Error getting from cache");
			e.printStackTrace();
			return null;
		}
	}
	
	
	protected CacheReturnValue getAnnotation() {
		return method.getAnnotation(CacheReturnValue.class);
	}
	
	private void mapMethodKeyToObject(Cacheable obj) {
		CacheableMapping mapping = new CacheableMapping(obj, getMappingKey());
		EZCache ez = new EZCache();
		ez.put(mapping);
	}
	
	private CacheableMapping getMappingToObject() {
		EZCache ez = new EZCache();
		CacheableMapping mapping = (CacheableMapping) ez.get(new EZCacheKey<CacheableMapping>(getMappingKey(), CacheableMapping.class));
		return mapping;
	}
	
	private SimpleCacheable<?> getSimpleCacheable() {
		EZCache ez = new EZCache();
		@SuppressWarnings("rawtypes")
		SimpleCacheable<?> simple = (SimpleCacheable) ez.get(new EZCacheKey<SimpleCacheable>(getMappingKey(), SimpleCacheable.class));
		return simple;
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
