package com.devinlynch.ezcache;

public interface Cacheable {
	public int getTimeToLiveSeconds();
	public int getTimeToIdleSeconds();
}
