package me.philcali.proxy.cache;

import static org.junit.Assert.assertEquals;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import me.philcali.cache.annotation.CacheDelete;
import me.philcali.cache.annotation.CacheGet;
import me.philcali.cache.annotation.CachePut;
import me.philcali.cache.api.Cache;
import me.philcali.cache.api.CacheItem;
import me.philcali.cache.api.CacheKey;
import me.philcali.cache.java.CacheMap;
import me.philcali.proxy.api.ProxyFactory;
import me.philcali.proxy.api.ProxyFactoryData;

public class CacheInterceptorTest {
    private Cache cache;
    private ProxyFactory factory;
    private Action action;

    public static interface Action {
        int get();

        int increment(int amount);

        void reset();
    }

    public static class BasicAction implements Action {
        private AtomicInteger counter = new AtomicInteger();

        @CacheGet("counter.key")
        @Override
        public int get() {
            return counter.get();
        }

        @CachePut("counter.key")
        @Override
        public int increment(int amount) {
            return counter.addAndGet(amount);
        }

        @CacheDelete("counter.key")
        @Override
        public void reset() {
            counter.set(0);
        }
    }

    @Before
    public void setUp() {
        action = new BasicAction();
        cache = new CacheMap();
        factory = ProxyFactoryData.builder()
                .addInterceptors(new CacheInterceptor(cache))
                .build();
    }

    @Test
    public void testInterception() {
        action = new BasicAction();
        action = factory.create(action, Action.class);
        assertEquals(0, action.get());
        assertEquals(Optional.ofNullable(0), cache.get(CacheKey.create("counter.key")).map(CacheItem::entity));
        assertEquals(2, action.increment(2));
        assertEquals(Optional.ofNullable(2), cache.get(CacheKey.create("counter.key")).map(CacheItem::entity));
        action.reset();
        assertEquals(Optional.empty(), cache.get(CacheKey.create("counter.key")));
    }
}
