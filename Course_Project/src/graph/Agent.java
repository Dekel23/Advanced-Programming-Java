package graph;

/**
 * Represents an agent that can subscribe to topics, receive messages,
 * and manage its own state.
 * <p>
 * Agents have a name, can reset their state, receive callbacks for published
 * messages, and can be closed or deactivated.
 * </p>
 */
public interface Agent {

    /**
     * Returns the name of the agent.
     * 
     * @return the name of the agent
     */
    String getName();

    /**
     * Resets the state of the agent to its initial condition.
     * <p>
     * This method is intended to clear or reinitialize any state held by the agent.
     * </p>
     */
    void reset();

    /**
     * Handles a callback from a topic when a message is published.
     * 
     * @param topic the name of the topic from which the message was published
     * @param msg the message that was published
     */
    void callback(String topic, Message msg);

    /**
     * Closes or deactivates the agent.
     * <p>
     * This method is intended to clean up any resources or connections used by the agent.
     * </p>
     */
    void close();

    /**
     * Compares this agent to another object for equality.
     * <p>
     * The default implementation of {@code equals} should be overridden
     * to provide meaningful comparison logic based on the agent's properties.
     * </p>
     * 
     * @param obj the object to be compared
     * @return {@code true} if this agent is equal to the specified object, {@code false} otherwise
     */
    @Override
    boolean equals(Object obj);
}
