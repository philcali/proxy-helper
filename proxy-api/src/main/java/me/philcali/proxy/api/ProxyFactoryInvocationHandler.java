package me.philcali.proxy.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class ProxyFactoryInvocationHandler implements InvocationHandler {
    private final Object concrete;
    private final List<Interceptor> interceptors;

    public ProxyFactoryInvocationHandler(final Object concrete, final List<Interceptor> interceptors) {
        this.concrete = concrete;
        this.interceptors = interceptors;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] params) throws Throwable {
        final MethodInvocation defaultMethod = new DefaultMethodInvocation(concrete, method);
        final MethodInvocation invocation = interceptors.stream().reduce(defaultMethod,
                (left, right) -> right.compose(left),
                (left, right) -> left);
        return invocation.invoke(params);
    }
}
