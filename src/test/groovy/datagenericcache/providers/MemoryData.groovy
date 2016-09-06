package datagenericcache.providers

import spock.lang.Specification

import java.time.Duration


class MemoryDataSpec  extends Specification {

    def "should return the same value informed to create the object"() {
        when:
        def memoryData = new MemoryData("value")

        then:
        assert memoryData.value == "value"
    }

    def "should not set time to expire when inform null duration value"() {
        when:
        def memoryData = new MemoryData("value")

        then:
        assert memoryData.expired == false
    }

    def "time to expire should return false when duration has not finished"() {
        when:
        def memoryData = new MemoryData("value", Duration.ofSeconds(1))

        then:
        assert memoryData.expired == false
    }

    def "time to expire should return true when duration has finished"() {
        when:
        def memoryData = new MemoryData("value", Duration.ofMillis(5))

        and:
        Thread.sleep(25)

        then:
        assert memoryData.expired == true
    }
}