package datagenericcache.clients

import datagenericcache.factories.CacheClientFactory
import datagenericcache.providers.CacheProvider
import spock.lang.Specification

import java.util.concurrent.Callable

class CacheClientSpec extends Specification {

    CacheProvider cacheClient;
    CacheProvider cacheProviderMock;

    def setup() {
        cacheProviderMock = Mock(CacheProvider)

        def cacheClientFactory = Stub(CacheClientFactory)
        cacheClientFactory.create(_) >> cacheProviderMock

        cacheClient = new CacheClient(cacheClientFactory)
    }

    def "should call add method in cache provider instance"() {
        when:
        cacheClient.add("key", "value", null)

        then:
        1 * cacheProviderMock.add("key", "value", _)
    }

    def "should call exists method in cache provider instance"() {
        when:
        cacheClient.exists("key")

        then:
        1 * cacheProviderMock.exists("key")
    }

    def "should call remove method in cache provider instance"() {
        when:
        cacheClient.remove("key")

        then:
        1 * cacheProviderMock.remove("key")
    }

    def "should call set method in cache provider instance"() {
        when:
        cacheClient.set("key", "value", null)

        then:
        1 * cacheProviderMock.set("key", "value", _)
    }

    def "should call retrieve method in cache provider instance"() {
        when:
        cacheClient.retrieve("key")

        then:
        1 * cacheProviderMock.retrieve("key")
    }

    def "should call retrieve or else method in cache provider instance"() {
        when:
        cacheClient.retrieveOrElse("key", null, [call: {value}] as Callable)

        then:
        1 * cacheProviderMock.retrieveOrElse("key", null, _)
    }

    def "should call flush or else method in cache provider instance"() {
        when:
        cacheClient.flush()

        then:
        1 * cacheProviderMock.flush()
    }
}