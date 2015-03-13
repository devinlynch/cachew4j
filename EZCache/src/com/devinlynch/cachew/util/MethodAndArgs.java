package com.devinlynch.cachew.util;

import java.lang.reflect.Method;

/**
 * Utility method for holding a method along with arguments for it
 * @author devinlynch
 *
 */
public class MethodAndArgs {
	public Method method;
	public Object[] args;
	public MethodAndArgs(Method method, Object[] args) {
		this.method = method;
		this.args = args;
	}
}
