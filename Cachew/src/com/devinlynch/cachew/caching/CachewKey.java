package com.devinlynch.cachew.caching;

import java.io.Serializable;

import com.devinlynch.cachew.interfaces.Cacheable;

public class CachewKey<CACHEABLE_CLASS_TYPE extends Cacheable> implements Serializable {
	private static final long serialVersionUID = 4627647677083982011L;
	private Class<? extends CACHEABLE_CLASS_TYPE> cacheableClass;
	private String key;
	
	public CachewKey(String key, Class<? extends CACHEABLE_CLASS_TYPE> clazz) {
		this.cacheableClass = clazz;
		this.key = key;
	}
	
	public Class<? extends CACHEABLE_CLASS_TYPE> getCacheableClass() {
		return cacheableClass;
	}

	public String getKey() {
		return key;
	}
	
	@Override
	public boolean equals(Object o) {
		if(! (o instanceof CachewKey))
			return false;
		
		CachewKey<?> k = (CachewKey<?>)o;
		return k.getClass().equals(getClass()) && k.getKey().equals(getKey());
	}
	
	@Override
	public int hashCode() {
		return ("key=["+getKey() + "]class=["+ cacheableClass.getName()+"]").hashCode();
	}
	
}
