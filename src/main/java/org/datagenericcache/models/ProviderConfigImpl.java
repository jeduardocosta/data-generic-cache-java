package org.datagenericcache.models;

import org.datagenericcache.factories.ConfigurationProviderFactory;
import org.cfg4j.provider.ConfigurationProvider;

public class ProviderConfigImpl implements ProviderConfig {
    private String host;
    private int portNumber;

    public ProviderConfigImpl(String bindingKey) {
        ConfigurationProvider configurationProvider = createConfigurationProvider();
        ProviderConfig providerConfig = configurationProvider.bind(bindingKey, ProviderConfig.class);
        this.host = providerConfig.host();
        this.portNumber = providerConfig.portNumber();
    }

    @Override
    public String host() {
        return host;
    }

    @Override
    public int portNumber() {
        return portNumber;
    }

    private ConfigurationProvider createConfigurationProvider() {
        return new ConfigurationProviderFactory().create();
    }
}