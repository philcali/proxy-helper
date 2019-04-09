package me.philcali.proxy.api;

import java.lang.reflect.Method;

@FunctionalInterface
public interface Interceptor {
    Object invoke(MethodInvocation method, Object[] params) throws Throwable;

    default MethodInvocation compose(final MethodInvocation other) {
        return new MethodInvocation() {
            @Override
            public Object implementation() {
                return other.implementation();
            }

            @Override
            public Method method() throws NoSuchMethodException, SecurityException {
                return other.method();
            }

            @Override
            public Object invoke(Object[] params) throws Throwable {
                return Interceptor.this.invoke(other, params);
            }
        };
    }
}
