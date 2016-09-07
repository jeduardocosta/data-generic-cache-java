package org.datagenericcache.factories;

import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.classpath.ClasspathConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;

import java.nio.file.Paths;
import java.util.Arrays;

public class ConfigurationProviderFactory {
    public ConfigurationProvider create() {
        ConfigFilesProvider configFilesProvider = () -> Arrays.asList(Paths.get("application.yaml"));

        ConfigurationSource source = new ClasspathConfigurationSource(configFilesProvider);

        return new ConfigurationProviderBuilder()
                .withConfigurationSource(source)
                .build();
    }
}