package com.devinlynch.ezcache.util;

import com.devinlynch.ezcache.interfaces.Cacheable;

/**
 * Utility class for mapping a custom key to a cached object
 * @author devinlynch
 *
 */
public class CacheableMapping implements Cacheable {
	private static final long serialVersionUID = 1L;
	private int timeToLive;
	private int timeToIdle;
	private String key;
	private String mappedObjectId;
	private Class<?> mappedObjectClass;
	
	public CacheableMapping(Cacheable cacheable, String customKey) {
		this.timeToIdle = cacheable.getTimeToIdleSeconds();
		this.timeToLive = cacheable.getTimeToLiveSeconds();
		this.mappedObjectId = cacheable.getCacheKey();
		this.mappedObjectClass = cacheable.getClass();
		this.key = customKey;
	}
	
	@Override
	public int getTimeToLiveSeconds() {
		return timeToLive;
	}

	@Override
	public int getTimeToIdleSeconds() {
		return timeToIdle;
	}

	@Override
	public String getCacheKey() {
		return key;
	}
	
	public String getMappedObjectId() {
		return mappedObjectId;
	}
	
	public Class<?> getMappedObjectClass() {
		return mappedObjectClass;
	}
}
