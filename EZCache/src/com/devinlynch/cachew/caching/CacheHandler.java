package com.devinlynch.cachew.caching;

import com.devinlynch.cachew.interfaces.Cacheable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.ObjectExistsException;

public class CacheHandler {	
	
	public CacheHandler() {
	}
	
	/**
	 * Actually puts a {@link Cacheable} object into the cache.
	 * @param val
	 * @param customKey
	 * @return
	 */
	private void putIntoCache(Cacheable val) {
		if(val == null || val.getCacheKey() == null)
			return;
		@SuppressWarnings({ "unchecked", "rawtypes" })
		CachewKey<?> objectKey = new CachewKey(val.getCacheKey(), val.getClass());
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
	private Object getFromCache(CachewKey<?> key) {
		if(key == null || key.getKey() == null)
			return null;
		Cache cache = getOrCreateCache(key.getCacheableClass());
		Element e = cache.get(key);
		if(e == null)
			return null;
		return e.getObjectValue();
	}
	
	private void deleteFromCache(CachewKey<?> key) {
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
	public<T> T get(CachewKey<? extends T> key) {
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
	public void delete(CachewKey<?> key) {
		if(key == null)
			return;
		deleteFromCache(key);
	}
	
	/**
	 * Delete an object from the cache  
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void delete(Cacheable obj) {
		if(obj.getCacheKey() == null)
			return;
		deleteFromCache(new CachewKey(obj.getCacheKey(), obj.getClass()));
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