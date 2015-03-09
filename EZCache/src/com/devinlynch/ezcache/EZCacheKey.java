package com.devinlynch.ezcache;

import java.io.Serializable;

public class EZCacheKey<CACHEABLE_CLASS_TYPE extends Cacheable> implements Serializable {
	private static final long serialVersionUID = 4627647677083982011L;
	private Class<? extends CACHEABLE_CLASS_TYPE> cacheableClass;
	private String key;
	
	public EZCacheKey(String key, Class<? extends CACHEABLE_CLASS_TYPE> clazz) {
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
		if(! (o instanceof EZCacheKey))
			return false;
		
		EZCacheKey<?> k = (EZCacheKey<?>)o;
		return k.getClass().equals(getClass()) && k.getKey().equals(getKey());
	}
	
	@Override
	public int hashCode() {
		return ("key=["+getKey() + "]class=["+ cacheableClass.getName()+"]").hashCode();
	}
	
}
