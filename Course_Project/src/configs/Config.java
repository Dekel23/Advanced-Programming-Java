package configs;

/**
 * Interface representing a configuration for setting up agents and topics.
 */
public interface Config {

    /**
     * Creates and configures agents and topics based on the implementation.
     */
    void create();

    /**
     * Gets the name of the configuration.
     *
     * @return the name of the configuration
     */
    String getName();

    /**
     * Gets the version of the configuration.
     *
     * @return the version number
     */
    int getVersion();
}
