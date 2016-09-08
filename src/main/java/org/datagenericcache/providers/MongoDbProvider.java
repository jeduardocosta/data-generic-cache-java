package org.datagenericcache.providers;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.datagenericcache.models.ProviderConfig;
import org.datagenericcache.models.ProviderConfigImpl;
import org.bson.Document;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

import static com.mongodb.client.model.Filters.eq;

public class MongoDbProvider implements CacheProvider {
    private final String COLLECTION_NAME = "datagenericcache_collection";
    private final int TIMEOUT_IN_SECONDS = 5;

    private MongoDatabase database;

    public MongoDbProvider() {
        ProviderConfig providerConfig = new ProviderConfigImpl("data_generic_cache_mongodb");
        MongoClient client = createMongoClient(providerConfig.host(), providerConfig.portNumber());
        createDatabase(client);
    }

    public MongoDbProvider(MongoClient mongoClient) {
        createDatabase(mongoClient);
    }

    public MongoDbProvider(String host, int portNumber) {
        createDatabase(createMongoClient(host, portNumber));
    }

    @Override
    public <T> void add(String key, T value, Duration duration) {
        Document document = createDocument(key, value, duration);
        database.getCollection(COLLECTION_NAME).insertOne(document);
    }

    @Override
    public <T> void set(String key, T value, Duration duration) {
        Document document = createDocument(key, value, duration);
        Document filter = new Document("key", key.toLowerCase());
        database.getCollection(COLLECTION_NAME).updateMany(filter, document);
    }

    @Override
    public void remove(String key) {
        database
                .getCollection(COLLECTION_NAME)
                .deleteOne(new Document("key", key.toLowerCase()));
    }

    @Override
    public boolean exists(String key) {
        Object retrieved = retrieve(key);
        return retrieved != null;
    }

    @Override
    public <T> T retrieve(String key) {
        Document document = database
                .getCollection(COLLECTION_NAME)
                .find(eq("key", key.toLowerCase()))
                .limit(1)
                .first();

        if (document == null) {
            return null;
        }

        if (document.get("time_to_expire") != null) {
            String timeToExpire = document.getString("time_to_expire");
            if (Instant.now().compareTo(Instant.parse(timeToExpire)) > 0) {
                remove(key);
                return null;
            }
        }

        return (T)document.get("value");
    }

    @Override
    public <T> T retrieveOrElse(String key, Duration duration, Callable<T> retrieveFunction) {
        T cachedObject = retrieve(key);

        if (cachedObject == null) {
            T retrievedObject = null;

            try {
                retrievedObject = retrieveFunction.call();
            } catch (Exception exception) {
            }

            if (retrievedObject == null) {
                return null;
            }

            add(key, retrievedObject, duration);
            cachedObject = retrievedObject;
        }

        return cachedObject;
    }

    @Override
    public void flush() {
        database.getCollection(COLLECTION_NAME).drop();
    }

    private <T> Document createDocument(String key, T value, Duration duration) {
        Document document = new Document()
                .append("key", key.toLowerCase())
                .append("value", value);

        if (duration != null) {
            document.append("time_to_expire", Instant.now().plus(duration).toString());
        }

        return document;
    }

    private MongoClient createMongoClient(String host, int portNumber) {
        ServerAddress serverAddress = new ServerAddress(host, portNumber);

        MongoClientOptions options = MongoClientOptions
                .builder()
                .connectTimeout(TIMEOUT_IN_SECONDS)
                .build();

        return new MongoClient(serverAddress, options);
    }

    private void createDatabase(MongoClient mongoClient) {
        database = mongoClient.getDatabase("datagenericcache_data");
    }
}