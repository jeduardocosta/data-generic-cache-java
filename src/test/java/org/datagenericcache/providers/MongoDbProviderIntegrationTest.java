package org.datagenericcache.providers;

import org.junit.*;
import org.testcontainers.containers.GenericContainer;

public class MongoDbProviderIntegrationTest extends CacheProviderIntegrationTest {
    @ClassRule
    public static GenericContainer container = new GenericContainer("mongo:3.2.9").withExposedPorts(27017);
    
    @Before
    public void before() {
        String host = container.getContainerIpAddress();
        int portNumber = container.getMappedPort(27017);
        super.cacheProvider = new MongoDbProvider(host, portNumber);
    }
}