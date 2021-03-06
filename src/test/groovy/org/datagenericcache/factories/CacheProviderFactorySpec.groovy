package org.datagenericcache.factories

import org.datagenericcache.factories.CacheProviderFactory
import org.datagenericcache.providers.MongoDbProvider
import org.datagenericcache.providers.RedisProvider
import spock.lang.Specification

class CacheProviderFactorySpec extends Specification {
    def "should return expected available providers in order"() {
        when:
        def factory = new CacheProviderFactory();

        and:
        def providerClasses = factory.create();

        and:
        def expected = [RedisProvider.class, MongoDbProvider.class]

        then:
        providerClasses == expected
    }
}