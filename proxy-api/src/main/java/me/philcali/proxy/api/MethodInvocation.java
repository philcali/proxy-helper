package me.philcali.proxy.api;

import java.lang.reflect.Method;

public interface MethodInvocation {
    Method method() throws NoSuchMethodException, SecurityException;

    Object implementation();

    Object invoke(Object[] params) throws Throwable;
}
