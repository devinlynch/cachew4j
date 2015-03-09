package com.devinlynch.ezcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.ObjectExistsException;

public class EZCache {	
	
	public EZCache() {
	}
	
	/**
	 * Actually puts a {@link Cacheable} object into the cache.
	 * @param val
	 * @param customKey
	 * @return
	 */
	private void putIntoCache(Cacheable val) {
		if(val == null)
			return;
		@SuppressWarnings({ "unchecked", "rawtypes" })
		EZCacheKey<?> objectKey = new EZCacheKey(val.getCacheKey(), val.getClass());
		Cache cache = getOrCreateCache(val.getClass());
		Element actualCacheElement = new Element(objectKey, val);
		actualCacheElement.setTimeToIdle(val.getTimeToIdleSeconds());
		actualCacheElement.setTimeToLive(val.getTimeToLiveSeconds());
		cache.put(actualCacheElement);
		
		return;
	}
	
	/**
	 * Actually gets an object from the cache given a key
	 * @param key
	 * @return
	 */
	private Object getFromCache(EZCacheKey<?> key) {
		Cache cache = getOrCreateCache(key.getCacheableClass());
		Element e = cache.get(key);
		if(e == null)
			return null;
		return e.getObjectValue();
	}
	
	private void deleteFromCache(EZCacheKey<?> key) {
		Cache cache = getOrCreateCache(key.getCacheableClass());
		cache.remove(key);
	}
	
	/**
	 * Gets the object from the cache given the key that would be returned by the objects 
	 * {@link Cacheable#getCacheKey()}
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public<T> T get(EZCacheKey<? extends T> key) {
		if(key == null)
			return null;
		return (T) getFromCache(key);
	}
	
	/**
	 * Put a object in the cache.  The key used will be the one defined by {@link Cacheable#getCacheKey()} 
	 * @param obj The object to put in the cache
	 */
	public void put(Cacheable obj) {
		putIntoCache(obj);
	}
	
	/**
	 * Delete an object from the cache 
	 */
	public void delete(EZCacheKey<?> key) {
		deleteFromCache(key);
	}
	
	/**
	 * Delete an object from the cache  
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void delete(Cacheable obj) {
		deleteFromCache(new EZCacheKey(obj.getCacheKey(), obj.getClass()));
	}
	
	public String getCacheName(Class<?> cachedObjectClass) {
		return "EZCache[REGCACHE:class="+cachedObjectClass.toString()+"]";
	}
	
	public Cache getOrCreateCache(Class<?> cachedObjectClass) {
		CacheManager cm = CacheManager.getInstance();
		
		Cache cache = cm.getCache(getCacheName(cachedObjectClass));
		if(cache == null) {
			try {
				cm.addCache(getCacheName(cachedObjectClass));
			} catch(ObjectExistsException e) {
			}
			cache = cm.getCache(getCacheName(cachedObjectClass));
		}
		return cache;
	}
	
}