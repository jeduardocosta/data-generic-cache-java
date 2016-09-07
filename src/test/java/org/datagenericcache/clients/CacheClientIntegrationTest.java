package org.datagenericcache.clients;

import org.datagenericcache.clients.CacheClient;
import org.datagenericcache.factories.CacheClientFactory;
import org.datagenericcache.factories.InstanceFactory;
import org.datagenericcache.providers.CacheProvider;
import org.datagenericcache.providers.LocalMemoryProvider;
import org.datagenericcache.providers.MongoDbProvider;
import org.datagenericcache.providers.RedisProvider;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.testcontainers.containers.GenericContainer;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CacheClientIntegrationTest {
    @ClassRule
    public static GenericContainer mongoDbContainer = new GenericContainer("mongo:3.2.9").withExposedPorts(27017);

    @ClassRule
    public static GenericContainer redisContainer = new GenericContainer("redis:3.0.7").withExposedPorts(6379);

    private CacheClient cacheClient;

    @Mock
    private InstanceFactory instanceFactoryMock;

    @Before
    public void setUp() {
        checkIfContainerIsRunning(mongoDbContainer, redisContainer);

        CacheProvider mongoDbProvider = createProvider(MongoDbProvider.class, mongoDbContainer, 27017);
        CacheProvider redisProvider = createProvider(RedisProvider.class, redisContainer, 6379);

        when(instanceFactoryMock.create(MongoDbProvider.class)).thenReturn(mongoDbProvider);
        when(instanceFactoryMock.create(RedisProvider.class)).thenReturn(redisProvider);

        CacheClientFactory cacheClientFactory = new CacheClientFactory(new LocalMemoryProvider(), instanceFactoryMock);
        cacheClient = new CacheClient(cacheClientFactory);
    }

    @Test
    public void shouldAddItemAndRetrieveItUsingTheSameProvider() {
        cacheClient.add("key_same_provider", "value", null);

        assertEquals("value", cacheClient.retrieve("key_same_provider"));
    }

    @Test
    public void shouldAddItemsAfterFirstProviderIsDown() {
        cacheClient.add("key_first_prousingCacheClientvider_down_1", "value", null);

        redisContainer.stop();

        cacheClient.add("key_first_provider_down_2", "value", null);

        assertNull(cacheClient.retrieve("key_first_provider_down_1"));
        assertEquals("value", cacheClient.retrieve("key_first_provider_down_2"));
    }

    @Test
    public void shouldContinueWorkingAfterAllProvidersDown() {
        cacheClient.add("key_all_providers_down_1", "value", null);

        redisContainer.stop();
        mongoDbContainer.stop();

        cacheClient.add("key_all_providers_down_2", "value", null);

        assertNull(cacheClient.retrieve("key_all_providers_down_1"));
        assertEquals("value", cacheClient.retrieve("key_all_providers_down_2"));
    }

    @Test
    public void shouldRetrieveNullWhenValueDoesntExistUsingCacheClient() {
        String obtained = cacheClient.retrieve("whenValueDoesntExistKey");

        assertNull(obtained);
    }

    @Test
    public void shouldAddAndRetrieveSuccessfullUsingCacheClient() {
        cacheClient.add("key", "value", Duration.ofSeconds(1));

        assertEquals("value", cacheClient.retrieve("key"));
    }

    @Test
    public void shouldAddWhenInvokingRetrieveOrElseUsingCacheClient() {
        cacheClient.retrieveOrElse("key", Duration.ofSeconds(1), () -> "value");

        assertEquals("value", cacheClient.retrieve("key"));
    }

    @Test
    public void shouldExpireWhenAddWithTimeSpanUsingCacheClient() {
        cacheClient.add("key", "value", Duration.ofMillis(50));

        await()
                .atMost(150, MILLISECONDS)
                .until(() -> cacheClient.retrieve("key") == null);
    }

    @Test
    public void shouldCheckIfValueExistsUsingCacheClient() {
        cacheClient.add("key", "value", Duration.ofSeconds(1));

        assertTrue(cacheClient.exists("key"));
    }

    @Test
    public void shouldRemoveValueUsingCacheClient() {
        cacheClient.add("key", "value", Duration.ofSeconds(1));
        cacheClient.remove("key");

        assertFalse(cacheClient.exists("key"));
    }

    private void checkIfContainerIsRunning(GenericContainer... containers) {
        for (GenericContainer container : containers) {
            if (!container.isRunning()) {
                container.start();
            }
        }
    }

    private CacheProvider createProvider(Class<?> type, GenericContainer container, int mappedPortNumber) {
        String host = container.getContainerIpAddress();
        int portNumber = container.getMappedPort(mappedPortNumber);

        CacheProvider cacheProvider = null;

        try {
            cacheProvider = (CacheProvider)type.getDeclaredConstructor(String.class, int.class).newInstance(host, portNumber);
        } catch (InstantiationException | IllegalAccessException |
                InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return cacheProvider;
    }
}