package com.devinlynch.ezcache.util;

import java.io.Serializable;

import com.devinlynch.ezcache.Cacheable;

public class SimpleCacheable<T extends Serializable> implements Cacheable {
	private static final long serialVersionUID = 1L;
	
	private String cacheKey;
	private int timeToLiveSeconds;
	private int timeToIdleSeconds;
	private T value;
	
	public SimpleCacheable(T value, String cacheKey, int timeToLiveSeconds, int timeToIdleSeconds) {
		this.value = value;
		this.cacheKey = cacheKey;
		this.timeToLiveSeconds = timeToLiveSeconds;
		this.timeToIdleSeconds = timeToIdleSeconds;
	}
	
	
	@Override
	public String getCacheKey() {
		return cacheKey;
	}

	@Override
	public int getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}

	@Override
	public int getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}
	
	public T getValue() {
		return value;
	}

}
