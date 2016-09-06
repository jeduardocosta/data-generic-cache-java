package datagenericcache.providers

import datagenericcache.providers.LocalMemoryProvider
import spock.lang.Specification
import java.time.Duration
import java.util.concurrent.Callable

class LocalMemoryProviderSpec extends Specification {

    def cacheProvider

    final def key = "key";
    final def value = "value";

    def setup() {
        cacheProvider = new LocalMemoryProvider()
    }

    def "should return null when inform invalid key"() {
        when:
        def obtained = cacheProvider.retrieve("whenValueDoesntExistKey")

        then:
        assert obtained == null
    }

    def "should add and retrieve item"() {
        when:
        cacheProvider.add(key, value, Duration.ofSeconds(1))

        and:
        def obtained = cacheProvider.retrieve(key)

        then:
        assert obtained == value
    }

    def "should add item when use retrieve or else"() {
        when:
        def obtained = cacheProvider.retrieveOrElse(key, Duration.ofSeconds(1), [call: {value}] as Callable)

        then:
        assert obtained == value
    }

    def "should expire item and return null"() {
        when:
        cacheProvider.add(key, value, Duration.ofMillis(1))

        and:
        Thread.sleep(10)

        and:
        def obtained = cacheProvider.retrieve(key);

        then:
        assert obtained == null
    }

    def "should check if value exists and return true"() {
        when:
        cacheProvider.add(key, value, Duration.ofSeconds(1))

        then:
        cacheProvider.exists(key) == true
    }

   def "should remove an existing item"() {
        when:
        cacheProvider.add(key, value, Duration.ofSeconds(1))

        and:
        cacheProvider.remove(key);

        then:
        cacheProvider.exists(key) == false
    }
}