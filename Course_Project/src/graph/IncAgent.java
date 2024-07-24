package graph;

/**
 * Represents an agent that increments a value received from an input topic
 * and publishes the incremented value to an output topic.
 * <p>
 * The agent is initialized with an array of subscriber topics and publisher topics.
 * It subscribes to the first topic in the subscriber array and publishes to the first
 * topic in the publisher array. The agent increments the value received from the input
 * topic and publishes the result to the output topic.
 * </p>
 */
public class IncAgent implements Agent {

    /**
     * The name of the agent.
     */
    private final String name = "Inc";

    /**
     * The name of the input topic to subscribe to.
     */
    private String input;

    /**
     * The name of the output topic to publish to.
     */
    private String output;

    /**
     * The current value being processed by the agent.
     */
    private double num;
    
    /**
     * Constructs an {@code IncAgent} with specified subscriber and publisher topics.
     * <p>
     * The agent subscribes to the first topic in the {@code subs} array and adds itself
     * as a publisher to the first topic in the {@code pubs} array. If the topics are not
     * available, appropriate messages are logged.
     * </p>
     * 
     * @param subs an array containing the names of topics to subscribe to
     * @param pubs an array containing the names of topics to publish to
     */
    public IncAgent(String[] subs, String[] pubs) {
        this.reset(); // Reset value to 0
        try {
            input = subs[0];
            TopicManagerSingleton.get().getTopic(this.input).subscribe(this); // Subscribe to input topic
        } catch (Exception e) {
            input = null;
            System.out.println("No subs for Agent: " + this.name);
        }
        try {
            output = pubs[0];
            TopicManagerSingleton.get().getTopic(this.output).addPublisher(this); // Add as publisher to output topic
        } catch (Exception e) {
            output = null;
            System.out.println("No pubs for Agent: " + this.name);
        }
    }

    /**
     * Returns the name of the agent.
     * 
     * @return the name of the agent
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Resets the internal state of the agent.
     * <p>
     * This method sets the current value to 0.
     * </p>
     */
    @Override
    public void reset() {
        this.num = 0; // Reset value to 0
    }

    /**
     * Handles a callback when a message is received from the input topic.
     * <p>
     * The agent updates its internal value based on the received message and,
     * if an output topic is set, publishes the incremented value to the output topic.
     * </p>
     * 
     * @param topic the name of the topic from which the message was received
     * @param msg the message that was received
     */
    @Override
    public void callback(String topic, Message msg) {
        double num = msg.asDouble;

        if (Double.isNaN(num)) // If message can't be interpreted as a double
            return;
        
        if (topic.equals(this.input)) { // If the topic is the input topic
            this.num = num; // Update the value
            System.out.println("Agent: " + this.name + " Changed to: " + this.num);
            if (this.output != null) // If an output topic is set, publish the incremented value
                TopicManagerSingleton.get().getTopic(this.output).publish(new Message(this.num + 1));
        }
    }

    /**
     * Closes the agent, unsubscribing from the input topic and removing itself
     * as a publisher from the output topic.
     * <p>
     * This method cleans up any subscriptions and publishers related to the agent.
     * </p>
     */
    @Override
    public void close() {
        // Unsubscribe from the input topic and remove as a publisher from the output topic
        if (this.input != null)
            TopicManagerSingleton.get().getTopic(this.input).unsubscribe(this);
        if (this.output != null)
            TopicManagerSingleton.get().getTopic(this.output).removePublisher(this);
    }

    /**
     * Compares this {@code IncAgent} to another object for equality.
     * <p>
     * Two {@code IncAgent} instances are considered equal if they have the same
     * input and output topics.
     * </p>
     * 
     * @param obj the object to be compared
     * @return {@code true} if this agent is equal to the specified object, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        IncAgent other = (IncAgent) obj;
        
        return input.equals(other.input) &&
               output.equals(other.output); // Return if same publishers and subscribers
    }
}