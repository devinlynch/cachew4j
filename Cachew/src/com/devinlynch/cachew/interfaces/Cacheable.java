package com.devinlynch.cachew.interfaces;

import java.io.Serializable;

public interface Cacheable extends Serializable, CacheKeyCompliant {
	public int getTimeToLiveSeconds();
	public int getTimeToIdleSeconds();
}
