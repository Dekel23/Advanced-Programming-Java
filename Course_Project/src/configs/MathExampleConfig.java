package configs;

import graph.BinOpAgent;

/**
 * Configuration class for demonstrating basic mathematical operations using {@code BinOpAgent}.
 * <p>
 * This configuration creates three {@code BinOpAgent} instances to perform addition, subtraction,
 * and multiplication operations. It connects them through topics to illustrate how agents interact
 * and produce results.
 * </p>
 */
public class MathExampleConfig implements Config {

    /**
     * Constructs a {@code MathExampleConfig} instance.
     * <p>
     * This default constructor is provided to allow instantiation of the class.
     * </p>
     */
    public MathExampleConfig() {
        // Default constructor
    }

    /**
     * Creates and initializes {@code BinOpAgent} instances with different mathematical operations.
     * <p>
     * This method sets up agents to perform addition, subtraction, and multiplication operations.
     * The results of these operations are published to topics that are connected to subsequent agents.
     * </p>
     */
    @Override
    public void create() {
        // Create BinOpAgent instances for various operations
        new BinOpAgent("plus", "A", "B", "R1", (x, y) -> x + y);
        new BinOpAgent("minus", "A", "B", "R2", (x, y) -> x - y);
        new BinOpAgent("mul", "R1", "R2", "R3", (x, y) -> x * y);
    }

    /**
     * Returns the name of this configuration.
     * 
     * @return the name of the configuration
     */
    @Override
    public String getName() {
        return "Math Example";
    }

    /**
     * Returns the version of this configuration.
     * 
     * @return the version number of the configuration
     */
    @Override
    public int getVersion() {
        return 1;
    }
}