package org.datagenericcache.providers;

import org.junit.Before;

public class LocalMemoryProviderTest extends CacheProviderIntegrationTest {
    @Before
    public void before() {
        super.cacheProvider = new LocalMemoryProvider();
    }
}