# cachew4j
Instead of having to crack the shell, just use cachew and get the nuts you use most often

## Why eat it?

Cachew is a caching framework that extends the functionality of Ehcache to make caching extremely clean and tasty.  It requires almost no configurations and the caching is done automatically for you!

## How to eat it?

1. First import the static Cachew methods:
```java
import static com.devinlynch.cachew.Cachew.*
```

2. Figure out what method you want the return value to be cached for.  Add the @CacheReturnValue annotation to this method
```java
public class FooService {
   @CacheReturnValue
   public void bar(String id) {
      // To some work and return a result
   }
}
```

3. Once you have an instance of the object that contains methods you want cached, we need to wrap it with a caching layer.  use the static cache(Object o) method.
```java
FooService objectThatHasCachingLayer = cache(new FooService());
```
