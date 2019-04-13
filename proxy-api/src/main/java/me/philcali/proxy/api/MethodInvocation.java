package me.philcali.proxy.api;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public interface MethodInvocation {
    Method method() throws NoSuchMethodException, SecurityException;

    Object implementation();

    Object invoke(Object[] params) throws Throwable;

    default Class<?> getRealClass() throws NoSuchMethodException, SecurityException {
        if (Proxy.isProxyClass(implementation().getClass())) {
            return method().getDeclaringClass();
        }
        return implementation().getClass();
    }
}
