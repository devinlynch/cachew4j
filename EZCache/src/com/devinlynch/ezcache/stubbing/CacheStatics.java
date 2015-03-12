package com.devinlynch.ezcache.stubbing;

import com.devinlynch.ezcache.EZCache;
import com.devinlynch.ezcache.EZCacheKey;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

public class CacheStatics {
	@SuppressWarnings("unchecked")
	public static<T> T mock(Class<T> clazz) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(NoOp.INSTANCE);
		return (T) enhancer.create();
	}
	
	public static void main(String[] args) {
		EZCache mock = mock(EZCache.class);
		mock.get(null);
	}
}


