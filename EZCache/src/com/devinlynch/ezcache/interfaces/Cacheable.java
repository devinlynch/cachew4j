package com.devinlynch.ezcache.interfaces;

import java.io.Serializable;

public interface Cacheable extends Serializable, CacheKeyCompliant {
	public int getTimeToLiveSeconds();
	public int getTimeToIdleSeconds();
}
