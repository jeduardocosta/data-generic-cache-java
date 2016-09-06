package datagenericcache.providers;

import com.alibaba.fastjson.JSON;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.time.Duration;
import java.util.concurrent.Callable;

public class MySqlProvider implements CacheProvider {
    private String databaseName;
    private Connection connection;

    public MySqlProvider(String connectionUrl) throws SQLException {
        try {
            connection = DriverManager.getConnection(connectionUrl);
            databaseName = connection.getCatalog();
        } catch (SQLException exception) {
            throw exception;
        }
    }

    @Override
    public <T> void add(String key, T value, Duration duration) {
        try {
            String json = JSON.toJSONString(value);
            PreparedStatement statement = connection.prepareStatement("insert into ? values ('?', '?')");
            statement.setString(1, databaseName);
            statement.setString(2, key.toLowerCase());
            statement.setString(3, json);
            statement.execute();

        } catch (SQLException e) {
        }
    }

    @Override
    public <T> void set(String key, T value, Duration duration) {
        throw new NotImplementedException();
    }

    @Override
    public void remove(String key) {
        throw new NotImplementedException();
    }

    @Override
    public boolean exists(String key) {
        throw new NotImplementedException();
    }

    @Override
    public <T> T retrieve(String key) {
        T result = null;

        try {
            PreparedStatement statement = connection.prepareStatement("select value from ? where key = '?'");
            statement.setString(1, databaseName);
            statement.setString(2, key.toLowerCase());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String json =resultSet.getString("value");
                result = (T)JSON.parse(json);
            }
        } catch (SQLException e) {
        }

        return result;
    }

    @Override
    public <T> T retrieveOrElse(String key, Duration duration, Callable<T> retrieveFunction) {
        throw new NotImplementedException();
    }
}