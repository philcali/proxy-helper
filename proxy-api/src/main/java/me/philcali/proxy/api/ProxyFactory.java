package me.philcali.proxy.api;

import java.lang.reflect.Proxy;
import java.util.List;

import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.Data;
import me.philcali.zero.lombok.annotation.NonNull;

@Data @Builder
public interface ProxyFactory {
    @NonNull
    List<Interceptor> getInterceptors();

    @SuppressWarnings("unchecked")
    default <C, I extends C> C create(final I impl, final Class<C> contract) {
        return (C) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] { contract },
                new ProxyFactoryInvocationHandler(impl, getInterceptors()));
    }
}
