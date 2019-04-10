# Proxy Helpers

This library facilitates the layers of method interception for Java interfaces.

## What?

Sometimes you want to layer higher level components with behaviors at runtime.
For example:

- A database component might want caching
- A implementation may want error translation
- Any component might need profiling

Java allows the ability to perform runtime decoration for these components using
the native `Proxy` object.

## How?

Java provides an injection point called an `InvocationHandler`, which is the
implementation for a proxy method call. In this implementation we are allowed
to inspect dynamic parameters that include the invoked method along with runtim
parameters passed to the method. This is a rich target for our method interception.

``` java
ProxyFactory proxies = ProxFactoryData.builder()
        .addInterceptor(CachingInterceptor.create(cache))
        .addInterceptor((method, params) -> {
            final long startTime = System.currentTimeMillis();
            try {
                return method.invoke(params);
            } finally {
                final long endTime = System.currentTimeMillis();
                LOGGER.info("Invoking {} took {} ms", method.method(), endTime - startTime);
            }
        })
        .build();
IContract cachingAndProfilingEntity = proxies.proxy(impl, IContract.class);
```
