package datagenericcache.models

import spock.lang.Specification

class ProviderConfigImplSpec extends Specification {
    def "should return expected values when create mongodb provider"() {
        when:
        def provider = new ProviderConfigImpl("data_generic_cache_mongodb")

        then:
        assert provider.host() == "127.0.0.1"

        and:
        assert provider.portNumber() == 27017
    }

    def "should return expected values when create redis provider"() {
        when:
        def provider = new ProviderConfigImpl("data_generic_cache_redis")

        then:
        assert provider.host() == "127.0.0.1"

        and:
        assert provider.portNumber() == 6379
    }
}
