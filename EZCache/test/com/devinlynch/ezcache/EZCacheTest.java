package com.devinlynch.ezcache;

import static com.devinlynch.ezcache.EZCache.cache;
import static org.junit.Assert.*;

import org.junit.Test;

import com.devinlynch.ezcache.annotations.CacheReturnValue;
import com.devinlynch.ezcache.interfaces.Cacheable;

/**
 * Obviously a lot more testing needs to be done...
 * @author devinlynch
 *
 */
public class EZCacheTest {

	
	
	public static class Bar implements Cacheable {
		private static final long serialVersionUID = 1L;
		private String text;
		
		public Bar(String t) {
			text = t;
		}
		
		@Override
		public String getCacheKey() {
			return text;
		}

		@Override
		public int getTimeToLiveSeconds() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getTimeToIdleSeconds() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
	public static interface IFoo {
		public Bar doit(String s);
		public String doit2(String s);
	}
	
	public static class Foo implements IFoo {
		int i = 0;
		int z = 0;
		@CacheReturnValue
		public Bar doit(String s) {
			return new Bar(s+(i++));
		}
		
		@CacheReturnValue
		public String doit2(String s) {
			return s + (z++);
		}
	} 
	
	@Test
	public void t1() {
		IFoo foo = cache(new Foo());
		
		String text1 = foo.doit("blah").text;
		String text2 = foo.doit("blah").text;
		
		assertEquals(text1, text2);
	}

}
