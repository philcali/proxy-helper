package me.philcali.proxy.cache;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.philcali.cache.annotation.CacheDelete;
import me.philcali.cache.annotation.CacheGet;
import me.philcali.cache.annotation.CachePut;
import me.philcali.cache.api.Cache;
import me.philcali.cache.api.CacheItem;
import me.philcali.cache.api.CacheItemData;
import me.philcali.cache.api.CacheKey;
import me.philcali.proxy.api.Interceptor;
import me.philcali.proxy.api.MethodInvocation;

public class CacheInterceptor implements Interceptor {
    private static final Pattern PARAM_PATTERN = Pattern.compile("\\{p(\\d+)\\}");
    private final Cache cache;

    public CacheInterceptor(final Cache cache) {
        this.cache = cache;
    }

    private CacheKey createKey(final String content, final Method method, final Object[] params) {
        if (content.isEmpty()) {
            return CacheKey.create(method.getName(), params);
        }
        final Matcher matcher = PARAM_PATTERN.matcher(content);
        final List<Object> transposedValues = new ArrayList<>();
        transposedValues.add(content);
        while (matcher.find()) {
            final int index = Integer.parseInt(matcher.group(1));
            transposedValues.add(params[index]);
        }
        return CacheKey.create(transposedValues.toArray());
    }

    @Override
    public Object invoke(final MethodInvocation method, final Object[] params) throws Throwable {
        final Method realMethod = method.method();
        final CacheGet get = realMethod.getAnnotation(CacheGet.class);
        final CachePut put = realMethod.getAnnotation(CachePut.class);
        final CacheDelete delete = realMethod.getAnnotation(CacheDelete.class);
        if (Objects.nonNull(get)) {
            final CacheKey key = createKey(get.value(), realMethod, params);
            Optional<CacheItem> cachedItem = cache.get(key);
            if (!cachedItem.isPresent()) {
                final CacheItem item = CacheItemData.builder()
                        .ttl(get.ttl())
                        .entity(method.invoke(params))
                        .build();
                cache.put(key, item);
                cachedItem = Optional.ofNullable(item);
            }
            return cachedItem.get().entity();
        } else if (Objects.nonNull(put)) {
            final CacheKey key = createKey(put.value(), realMethod, params);
            final Object rval = method.invoke(params);
            cache.put(key, CacheItemData.builder()
                    .entity(rval)
                    .ttl(put.ttl())
                    .build());
            return rval;
        } else if (Objects.nonNull(delete)) {
            final CacheKey key = createKey(delete.value(), realMethod, params);
            final Object rval = method.invoke(params);
            cache.remove(key);
            return rval;
        }
        return method.invoke(params);
    }
}
