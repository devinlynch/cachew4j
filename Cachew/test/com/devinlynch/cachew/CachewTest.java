package com.devinlynch.cachew;

import static com.devinlynch.cachew.Cachew.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.devinlynch.cachew.annotations.CacheReturnValue;
import com.devinlynch.cachew.interfaces.Cacheable;

/**
 * Obviously a lot more testing needs to be done...
 * @author devinlynch
 *
 */
public class CachewTest {

	
	
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
	public void testWrappedCache() {
		IFoo foo = cache(new Foo());
		
		String text1 = foo.doit("blah").text;
		String text2 = foo.doit("blah").text;
		
		assertEquals(text1, text2);
	}

	@Test
	public void testDelete() {
		IFoo foo = cache(new Foo());
		Foo mock = mock(new Foo());
		forKeyGeneratedBy(mock.doit("blah")).delete();
		
		String text1 = foo.doit("blah").text;
		
		forKeyGeneratedBy(mock.doit("blah")).delete();
		
		String text2 = foo.doit("blah").text;
		
		assertNotEquals(text1, text2);
	}
	
	@Test
	public void testPut() {
		IFoo foo = cache(new Foo());
		Foo mock = mock(new Foo());
		final String key ="testPut";
		final String val = "asdf";
		forKeyGeneratedBy(mock.doit(key)).delete();
		forKeyGeneratedBy(mock.doit(key)).put(new Bar(val));
		
		String text2 = foo.doit(key).text;
		
		assertEquals(val, text2);
	}
	
	@Test
	public void testGet() {
		Foo mock = mock(new Foo());
		final String key ="testGet";
		final String val = "asdf";
		forKeyGeneratedBy(mock.doit(key)).delete();
		forKeyGeneratedBy(mock.doit(key)).put(new Bar(val));
		String text2 = ((Bar) forKeyGeneratedBy(mock.doit(key)).get()).text;
		
		assertEquals(val, text2);
	}
}
