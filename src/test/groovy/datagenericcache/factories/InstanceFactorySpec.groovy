package datagenericcache.factories

import datagenericcache.models.MemoryData
import spock.lang.Specification

class InstanceFactorySpec extends Specification {
    def "should create instance with parameterless constructor using class reference"() {
        when:
        def instance = new InstanceFactory().create(CacheClientFactory.class)

        then:
        assert instance != null

        and:
        assert instance as CacheClientFactory
    }

    def "should not throw and return null when creating instance which has constructor with parameters"() {
        when:
        def instance = new InstanceFactory().create(MemoryData.class)

        then:
        notThrown()

        and:
        assert instance == null
    }
}