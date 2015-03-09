package com.devinlynch.ezcache;

import java.io.Serializable;

public interface Cacheable extends Serializable, CacheKeyCompliant {
	public int getTimeToLiveSeconds();
	public int getTimeToIdleSeconds();
}
