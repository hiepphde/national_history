package config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Properties properties = new Properties();
    private static final String DEFAULT_CONFIG_FILE = "application.properties";

    // Private constructor để ngăn instantiation
    private AppConfig() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE)) {
            if (input == null) {
                throw new FileNotFoundException("property file not found in the classpath: " + DEFAULT_CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load: " + DEFAULT_CONFIG_FILE, e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
