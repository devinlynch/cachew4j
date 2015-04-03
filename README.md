# cachew4j
Instead of having to crack the shell, just use cachew and get the nuts you use most often.

## Why eat it?

Cachew is a caching framework that extends the functionality of Ehcache to make caching extremely clean and tasty.  It requires almost no configurations and the caching is done automatically for you!  It is based on the idea that things that you want cached usually come from a specific method.  For example when you have a service object, you may want to cache the result of one of the object's methods which gets some data from a persitance store.  The typical workflow for adding caching to this method would be:

1. At the beginning of the method check if the object is in cache.  If so, return it.
2. Otherwise, do some work and then store the object in a cache
 
Cachew removes this manual labour and does the caching behind the scenes with the aid of proxies.  This style of programming is known as aspect-oriented programming.  

## How to eat it?

1. First import the static Cachew methods:
   ```java
   import static com.devinlynch.cachew.Cachew.*
   ```

2. Figure out what method you want the return value to be cached for.  Add the @CacheReturnValue annotation to this method
   ```java
   public class FooService {
      int counter = 0;
      @CacheReturnValue
      public Integer bar(String id) {
         counter++;
         return counter;
      }
   }
   ```

3. Once you have an instance of the object that contains methods you want cached, we need to wrap it with a caching layer.  Use the static cache(Object o) method.
   ```java
   FooService objectThatHasCachingLayer = cache(new FooService());
   ```
   
4. Now invoke the method that you enabled caching for.  The first time you call it, the value that is returned will be stored in cache mapped to the parameters given to the method.  The second time you call it, with the same parameters as the first time, the object will be retrieved from cache.  Awesome, eh!?
   ```java
   FooService objectThatHasCachingLayer = cache(new FooService());
   Integer result1 = objectThatHasCachingLayer.bar("1");
   Integer result2 = objectThatHasCachingLayer.bar("1");
   assertEquals(result1, result2); // This will assert true, I promise.
   ```

## Deleting
 
1. Wrap your object with a caching layer and invoke a method to cause the result to be stored in cache
   ```java
   FooService objectThatHasCachingLayer = cache(new FooService());
   Integer result = objectThatHasCachingLayer.bar("1234");
   // The result of bar("1234") is stored in cache for the next time its invoked
   ```
2. Create a mock object of a new instance of the class of your object from step 1
   ```java
   FooService mock = mock(new FooService());
   ```
3. Delete from the cache through your mock object with the same arguments given in step 1
   ```java
   forKeyGeneratedBy(mock.bar("1234")).delete();
   // The cache will now be empty
   ```


1. First import the static Cachew methods:
   ```java
   FooService objectThatHasCachingLayer = cache(new FooService());
   ```
