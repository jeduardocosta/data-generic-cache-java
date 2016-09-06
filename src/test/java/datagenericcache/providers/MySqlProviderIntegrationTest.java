package datagenericcache.providers;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.testcontainers.containers.GenericContainer;

import java.sql.SQLException;

public class MySqlProviderIntegrationTest /*extends CacheProviderIntegrationTest*/ {
    @ClassRule
    public static GenericContainer mysqlContainer = new GenericContainer("mysql:latest").withExposedPorts(3307);
    
    @Before
    public void before() throws SQLException, ClassNotFoundException {
        String host = mysqlContainer.getContainerIpAddress();
        String portNumber = mysqlContainer.getMappedPort(3307).toString();

        /*
        String jdbcConnection = String.format("jdbc:mysql//%s:%s", host, portNumber);
        super.cacheProvider = new MySqlProvider(jdbcConnection);
        */
    }
}