package com.devinlynch.cachew.util;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.devinlynch.cachew.annotations.CacheReturnValue;
import com.devinlynch.cachew.caching.CacheHandler;
import com.devinlynch.cachew.caching.CachewKey;
import com.devinlynch.cachew.interfaces.CacheKeyCompliant;
import com.devinlynch.cachew.interfaces.Cacheable;

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
			CacheHandler ezCache = new CacheHandler();
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
			CacheHandler ezCache = new CacheHandler();
			CacheableMapping mappingToObject = getMappingToObject();
			if(mappingToObject != null) {
				// First check to see if we have a mapping stored from the method to a cached object.  If so, we can return
				// the mapped object
				@SuppressWarnings({ "rawtypes", "unchecked" })
				Object object = ezCache.get(new CachewKey(mappingToObject.getMappedObjectId(), mappingToObject.getMappedObjectClass()));
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void delete() {
		try {
			CacheHandler ezCache = new CacheHandler();
			CacheableMapping mappingToObject = getMappingToObject();
			if(mappingToObject != null) {
				// If there is a mapping to another object, delete the mapping then delete the linked object
				ezCache.delete(new CachewKey(getMappingKey(), CacheableMapping.class));
				ezCache.delete(new CachewKey(mappingToObject.getMappedObjectId(), mappingToObject.getMappedObjectClass()));
			} else {
				// Otherwise delete the simple cacheable object
				deleteSimpleCacheable();
			}
		} catch (Exception e) {
			System.out.println("Error deleting from cache");
			e.printStackTrace();
		}
	}
	
	
	protected CacheReturnValue getAnnotation() {
		return method.getAnnotation(CacheReturnValue.class);
	}
	
	private void mapMethodKeyToObject(Cacheable obj) {
		CacheableMapping mapping = new CacheableMapping(obj, getMappingKey());
		CacheHandler ez = new CacheHandler();
		ez.put(mapping);
	}
	
	private CacheableMapping getMappingToObject() {
		CacheHandler ez = new CacheHandler();
		CacheableMapping mapping = (CacheableMapping) ez.get(new CachewKey<CacheableMapping>(getMappingKey(), CacheableMapping.class));
		return mapping;
	}
	
	private SimpleCacheable<?> getSimpleCacheable() {
		CacheHandler ez = new CacheHandler();
		@SuppressWarnings("rawtypes")
		SimpleCacheable<?> simple = (SimpleCacheable) ez.get(new CachewKey<SimpleCacheable>(getMappingKey(), SimpleCacheable.class));
		return simple;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void deleteSimpleCacheable() {
		CacheHandler ez = new CacheHandler();
		ez.delete(new CachewKey(getMappingKey(), SimpleCacheable.class));
	}
	
	/**
	 * Gets a unique key for the method and the values of its arguments.
	 * @return
	 */
	protected String getMappingKey() {
		String ps = "";
		for(Class<?> c : method.getParameterTypes()) {
			ps += c.getName();
		}
		String a = "";
		if(args != null) {
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
		}
		return "EZCache[METHODMAPPING:methodHashCode=[hc=["+method.hashCode()+"]numP=["+method.getParameterTypes().length+"]ps=["+ps+"]args=["+a+"]]]";	
	}
	

}
