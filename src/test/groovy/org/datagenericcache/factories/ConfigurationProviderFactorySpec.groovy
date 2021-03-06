package org.datagenericcache.factories

import org.datagenericcache.factories.ConfigurationProviderFactory
import spock.lang.Specification

class ConfigurationProviderFactorySpec extends Specification {
    def "should create configuration provider instance from application yaml file"() {
        when:
        def configurationProvider = new ConfigurationProviderFactory().create()

        then:
        assert configurationProvider != null
    }
}