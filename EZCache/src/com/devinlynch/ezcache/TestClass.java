package com.devinlynch.ezcache;

import java.lang.reflect.Method;

public class TestClass {
	
	private class Foo {
		public void doit() {
			
		}
		public void doit(String s) {
			
		}
	}
	
	public static void main(String[] args) {
		Method[] methods = Foo.class.getMethods();
		
		System.out.println(getMappingCacheName(methods[0]));
		System.out.println(getMappingCacheName(methods[1]));
	}
	
	private static String getMappingCacheName(Method method) {
		String ps = "";
		for(Class<?> c : method.getParameterTypes()) {
			ps += c.getName();
		}
		
		return "EZCache[METHODMAPPING:methodHashCode=[hc=["+method.hashCode()+"]numP=["+method.getParameterTypes().length+"]ps=["+ps+"]]]";
	}
}
