package me.philcali.proxy.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

public class ProxyFactoryTest {
    private ProxyFactory factory;
    private AtomicInteger suiteCounter;

    public static interface Action {
        int increment(int amount);
    }

    public static class BasicAction implements Action {
        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public int increment(int amount) throws IllegalStateException {
            return counter.addAndGet(amount);
        }
    }

    @Before
    public void setUp() {
        suiteCounter = new AtomicInteger();
        factory = ProxyFactoryData.builder()
                .addInterceptors((method, params) -> {
                    suiteCounter.incrementAndGet();
                    return method.invoke(params);
                })
                .addInterceptors((method, params) -> {
                    final Object rval = method.invoke(params);
                    if ((int) rval > 5) {
                        throw new IllegalStateException();
                    }
                    return rval;
                })
                .build();
    }

    @Test
    public void testProxy() {
        Action proxied = factory.create(new BasicAction(), Action.class);
        assertEquals(2, proxied.increment(2));
        assertEquals(1, suiteCounter.get());
        assertEquals(4, proxied.increment(2));
        assertEquals(2, suiteCounter.get());
        try {
            proxied.increment(2);
            fail("Should not have gotten here.");
        } catch (IllegalStateException ise) {
            assertEquals(3, suiteCounter.get());
        }
    }
}
