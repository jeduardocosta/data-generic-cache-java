package org.datagenericcache.providers;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MongoDbTest {
    private final String COLLECTION_NAME = "datagenericcache_collection";
    private final String key = "key";
    private final String value = "value";

    private CacheProvider cacheProvider;

    @Mock
    private MongoClient mongoClientMock;

    @Mock
    private MongoDatabase mongoDatabaseMock;

    @Mock
    private MongoCollection<Document> mongoCollectionMock;

    @Before
    public void setUp() {
        when(mongoClientMock.getDatabase("datagenericcache_data")).thenReturn(mongoDatabaseMock);
        when(mongoDatabaseMock.getCollection(COLLECTION_NAME)).thenReturn(mongoCollectionMock);

        cacheProvider = new MongoDbProvider(mongoClientMock);
    }

    @Test
    public void shouldCreateExpectedDocumentWhenAddDataWithNullDuration() {
        cacheProvider.add(key, value, null);

        Document expected = new Document()
                .append("key", key.toLowerCase())
                .append("value", value);

        verify(mongoCollectionMock, times(1)).insertOne(expected);
    }

    @Test
    public void shouldCallUpdateManyWithFilterWhenSetData() {
        cacheProvider.set(key, value, null);

        Document expected = new Document("key", key.toLowerCase());

        verify(mongoCollectionMock, times(1)).updateMany(eq(expected), any(Document.class));
    }

    @Test
    public void shouldCallDeleteOneWhenRemoveData() {
        cacheProvider.remove(key);

        Document expected = new Document("key", key.toLowerCase());

        verify(mongoCollectionMock, times(1)).deleteOne(eq(expected));
    }

    @Test
    public void shouldCallGetCollectionWhenFlushData() {
        cacheProvider.flush();

        verify(mongoDatabaseMock, times(1)).getCollection(COLLECTION_NAME);
    }

    @Test
    public void shouldCallDropInMongoCollectionWhenFlushData() {
        cacheProvider.flush();

        verify(mongoCollectionMock, times(1)).drop();
    }
}