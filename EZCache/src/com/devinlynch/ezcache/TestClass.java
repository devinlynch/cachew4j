package com.devinlynch.ezcache;

import java.lang.reflect.Method;

public class TestClass {
	
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
	
	public static void main(String[] args) {
		IFoo foo = CacheWrapper.wrapObject(new Foo(), IFoo.class, Foo.class);
		
		System.out.println(foo.doit("blah").text);
		System.out.println(foo.doit("blah").text);
		System.out.println(foo.doit("foo").text);
		System.out.println(foo.doit("foo").text);
		System.out.println(foo.doit("blah").text);
		
		System.out.println("------");
		System.out.println(foo.doit2("blah"));
		System.out.println(foo.doit2("blah"));
		System.out.println(foo.doit2("blah2"));
		System.out.println(foo.doit2("blah"));
	}
	

}
