package graph;

/**
 * Represents an agent that indexes a list of strings based on an integer value
 * and publishes the result to an output topic.
 * <p>
 * The agent subscribes to two input topics: the first topic provides a comma-separated
 * list of strings, and the second topic provides an integer index. The agent uses the
 * index to select a string from the list and publishes the selected string to an output
 * topic.
 * </p>
 */
public class IndexAgent implements Agent {

    /**
     * The name of the agent.
     */
    private final String name = "Index";

    /**
     * The name of the first input topic to subscribe to.
     */
    private String input1;

    /**
     * The name of the second input topic to subscribe to.
     */
    private String input2;

    /**
     * The name of the output topic to publish to.
     */
    private String output;

    /**
     * The list of strings received from the first input topic.
     */
    private String[] list;

    /**
     * The current index received from the second input topic.
     */
    private int idx;

    /**
     * Constructs an {@code IndexAgent} with specified subscriber and publisher topics.
     * <p>
     * The agent subscribes to the first two topics in the {@code subs} array and adds
     * itself as a publisher to the first topic in the {@code pubs} array. If any of the
     * topics are not available, appropriate messages are logged.
     * </p>
     * 
     * @param subs an array containing the names of topics to subscribe to
     * @param pubs an array containing the names of topics to publish to
     */
    public IndexAgent(String[] subs, String[] pubs) {
        this.reset(); // Reset values to default

        try {
            input1 = subs[0];
            TopicManagerSingleton.get().getTopic(this.input1).subscribe(this); // Subscribe to first input topic
        } catch (Exception e) {
            input1 = null;
            System.out.println("No subs for Agent: " + this.name);
        }
        try {
            input2 = subs[1];
            TopicManagerSingleton.get().getTopic(this.input2).subscribe(this); // Subscribe to second input topic
        } catch (Exception e) {
            input2 = null;
            System.out.println("Only 1 subs for Agent: " + this.name);
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
     * This method initializes the list to an empty array and sets the index to -1.
     * </p>
     */
    @Override
    public void reset() {
        this.list = new String[0]; // Initialize list as empty
        this.idx = -1; // Initialize index to -1
    }

    /**
     * Handles a callback when a message is received from a topic.
     * <p>
     * The agent updates its list or index based on the topic of the received message.
     * It then publishes the selected string from the list based on the index to the
     * output topic. If the index is out of bounds, an "error" message is published.
     * </p>
     * 
     * @param topic the name of the topic from which the message was received
     * @param msg the message that was received
     */
    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(this.input1)) { // If the topic is the first input
            this.list = msg.asText.split(","); // Update the list
        }
        
        if (topic.equals(this.input2)) { // If the topic is the second input
            double num = msg.asDouble;
            if (Double.isNaN(num) || (int)num != num) // If message can't be interpreted as a valid integer
                return;
            this.idx = (int)num; // Update the index
        }

        System.out.println("Agent: " + this.name + " Changed to: " + String.join(", ", this.list) + " Index: " + this.idx);
        
        if (this.output != null) { // If an output topic is set, publish the selected string
            try {
                if (this.idx >= 0 && this.idx < this.list.length) {
                    TopicManagerSingleton.get().getTopic(this.output).publish(new Message(this.list[this.idx]));
                } else {
                    TopicManagerSingleton.get().getTopic(this.output).publish(new Message("error")); // Index out of bounds
                }
            } catch (Exception e) {
                TopicManagerSingleton.get().getTopic(this.output).publish(new Message("error")); // Publishing error
            }
        }
    }

    /**
     * Closes the agent, unsubscribing from the input topics and removing itself
     * as a publisher from the output topic.
     * <p>
     * This method cleans up any subscriptions and publishers related to the agent.
     * </p>
     */
    @Override
    public void close() {
        // Unsubscribe from input topics and remove as a publisher from the output topic
        if (this.input1 != null)
            TopicManagerSingleton.get().getTopic(this.input1).unsubscribe(this);
        if (this.output != null)
            TopicManagerSingleton.get().getTopic(this.output).removePublisher(this);
        if (this.input2 != null)
            TopicManagerSingleton.get().getTopic(this.input2).unsubscribe(this);
    }

    /**
     * Compares this {@code IndexAgent} to another object for equality.
     * <p>
     * Two {@code IndexAgent} instances are considered equal if they have the same
     * input topics and output topic.
     * </p>
     * 
     * @param obj the object to be compared
     * @return {@code true} if this agent is equal to the specified object, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        IndexAgent other = (IndexAgent) obj;
        
        return this.input1.equals(other.input1) &&
               this.input2.equals(other.input2) &&
               this.output.equals(other.output); // Return if same publishers and subscribers
    }
}