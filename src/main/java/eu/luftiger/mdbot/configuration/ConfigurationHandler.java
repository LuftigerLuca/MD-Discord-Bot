package eu.luftiger.mdbot.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import eu.luftiger.mdbot.Bot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class is responsible for loading the configuration from the file system.
 */
public class ConfigurationHandler {

    private final ObjectMapper objectMapper;
    private Configuration configuration;

    public ConfigurationHandler(Bot bot) {
        this.objectMapper = new ObjectMapper(new YAMLFactory());
    }

    /**
     * Loads the configuration from the file
     * @throws IOException if an error occurs
     */
    public void loadConfiguration() throws IOException {
        File configurationFile = new File("config.yaml");
        if(!configurationFile.exists()){
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.yaml");
            Files.copy(inputStream, Paths.get("config.yaml"));
        }
        configuration = objectMapper.readValue(configurationFile, Configuration.class);
    }


    /**
     * Returns the loaded configuration
     * @return the loaded configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }
}
