package com.devinlynch.ezcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Implementations of this class handle putting / getting from a cache.  The purpose
 * is to be used in conjunction with the {@link Cacheable} annotation.  To have a method
 * supported for caching, you first describe it with the {@link Cacheable} annotation. You
 * then create a subclass of this class which will be in charge of putting and getting
 * from the cache.  Set the {@link Cacheable#cacheClass()} to be your created subclass.  You
 * then must implement the {@link AbstractCache#getCacheKeyGivenArgs(Object[])} to return a 
 * unique cache key given the arguments that were given to the method that was described with
 * the {@link Cacheable} annotation.  The final step for enabling caching is to wrap the object
 * that contains the method you want cached using {@link CacheWrapper#wrapObject(Object)}.
 * @author devinlynch
 */
public abstract class AbstractCache<OBJECT_BEING_CACHED_TYPE> {
	/**
	 * The name of the cache as defined in your ehcache.xml file.  If the cache is not defined
	 * then there will be <b>NO</b> caching taking place.
	 * @return
	 */
	public abstract String getCacheName();
	
	/**
	 * Actually puts an object into the cache given a key
	 * @param key
	 * @param val
	 * @return
	 */
	protected boolean putIntoCache(String key, Object val) {
		CacheManager cm = CacheManager.getInstance();
		Cache cache = cm.getCache(getCacheName());
		if(cache == null)
			return false;
		
		Element e = new Element(key, val);
		cache.put(e);
		return true;
	}
	
	/**
	 * Actually gets an object from the cache given a key
	 * @param key
	 * @return
	 */
	protected Object getFromCache(Object key) {
		CacheManager cm = CacheManager.getInstance();
		Cache cache = cm.getCache(getCacheName());
		if(cache == null)
			return null;
		
		Element e = cache.get(key);
		if(e == null)
			return null;
		return e.getObjectValue();
	}
	
	/**
	 * Gets a object stored in the cache given the arguments that were given to the method
	 * described with the {@link Cacheable} annotation.
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public OBJECT_BEING_CACHED_TYPE get(Object[] args) {
		String key = getCacheKeyGivenArgs(args);
		if(key == null)
			return null;
		return (OBJECT_BEING_CACHED_TYPE) getFromCache(key);
	}
	
	/**
	 * Put a object in the cache given the arguments that were given to the method described
	 * with the {@link Cacheable} annotation.
	 * @param args The arguments given to the method described with {@link Cacheable}
	 * @param obj The obejct to put in the cache
	 * @return
	 */
	public boolean put(Object[] args, OBJECT_BEING_CACHED_TYPE obj) {
		String key = getCacheKeyGivenArgs(args);
		if(key == null)
			return false;
		return putIntoCache(key, obj);
	}
	
	/**
	 * Get a unique cache key given arguments.
	 * @param args
	 * @return
	 */
	protected abstract String getCacheKeyGivenArgs(Object[] args);
}