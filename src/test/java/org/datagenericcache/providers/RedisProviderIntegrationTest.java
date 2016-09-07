package org.datagenericcache.providers;

import org.junit.Before;
import org.junit.ClassRule;
import org.testcontainers.containers.GenericContainer;

public class RedisProviderIntegrationTest extends CacheProviderIntegrationTest {
    @ClassRule
    public static GenericContainer redisContainer= new GenericContainer("redis:3.0.7").withExposedPorts(6379);

    @Before
    public void before() {
        String host = redisContainer.getContainerIpAddress();
        int portNumber = redisContainer.getMappedPort(6379);
        super.cacheProvider = new RedisProvider(host, portNumber);
    }
}