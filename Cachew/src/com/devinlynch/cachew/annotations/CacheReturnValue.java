package com.devinlynch.cachew.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheReturnValue {
	public int timeToLiveSeconds() default 30;
	public int timeToIdleSeconds() default 30;
}