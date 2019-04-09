package me.philcali.proxy.api;

import java.lang.reflect.Method;

public class DefaultMethodInvocation implements MethodInvocation {
    private final Object implementation;
    private final Method method;

    public DefaultMethodInvocation(final Object implementation, final Method method) {
        this.implementation = implementation;
        this.method = method;
    }

    @Override
    public Object invoke(final Object[] params) throws Throwable {
        return method.invoke(implementation, params);
    }

    @Override
    public Object implementation() {
        return implementation;
    }

    @Override
    public Method method() throws NoSuchMethodException, SecurityException {
        return implementation.getClass().getMethod(method.getName(), method.getParameterTypes());
    }
}
