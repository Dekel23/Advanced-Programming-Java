package graph;

import java.util.function.BinaryOperator;

/**
 * Represents an agent that performs a binary operation on two input values
 * and publishes the result to an output topic.
 * <p>
 * The agent subscribes to two input topics to receive values, applies a binary
 * operation on these values, and publishes the result to an output topic. The agent
 * is identified by a name and can reset its internal state or be closed to unsubscribe
 * and remove itself from topics.
 * </p>
 */
public class BinOpAgent implements Agent {

    /**
     * The name of the agent.
     */
    private final String name;

    /**
     * The name of the first input topic.
     */
    private String input1;

    /**
     * The name of the second input topic.
     */
    private String input2;

    /**
     * The name of the output topic.
     */
    private String output;

    /**
     * The value of the first input as a double.
     */
    private double num1;

    /**
     * The value of the second input as a double.
     */
    private double num2;

    /**
     * The binary operation to be applied to the two input values.
     */
    private BinaryOperator<Double> op;

    /**
     * Constructs a {@code BinOpAgent} with the specified parameters.
     * <p>
     * The agent is initialized with names for the input and output topics and
     * a binary operation. It subscribes to the input topics and adds itself
     * as a publisher to the output topic.
     * </p>
     * 
     * @param name the name of the agent
     * @param input1 the name of the first input topic
     * @param input2 the name of the second input topic
     * @param output the name of the output topic
     * @param op the binary operation to apply to the input values
     */
    public BinOpAgent(String name, String input1, String input2, String output, BinaryOperator<Double> op) {
        this.name = name;
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.op = op;
        this.reset(); // Reset values to 0
        TopicManagerSingleton.get().getTopic(this.input1).subscribe(this);
        TopicManagerSingleton.get().getTopic(this.input2).subscribe(this);
        TopicManagerSingleton.get().getTopic(this.output).addPublisher(this);
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
     * This method sets the internal values of the inputs to 0.
     * </p>
     */
    @Override
    public void reset() {
        this.num1 = 0; // Reset value of the first input
        this.num2 = 0; // Reset value of the second input
    }

    /**
     * Handles a callback when a message is received from a topic.
     * <p>
     * The agent updates its internal values based on the topic of the message
     * and applies the binary operation if both input values are available. 
     * The result is then published to the output topic.
     * </p>
     * 
     * @param topic the name of the topic from which the message was received
     * @param msg the message that was received
     */
    @Override
    public void callback(String topic, Message msg) {
        double num = msg.asDouble;
        boolean update = false;
        if (Double.isNaN(num)) // If message can't be interpreted as a double
            return;

        if (topic.equals(this.input1)) { // If the topic is the first input
            this.num1 = num; // Update the first value
            update = true;
        }

        if (topic.equals(this.input2)) { // If the topic is the second input
            this.num2 = num; // Update the second value
            update = true;
        }

        if (update) { // If any input value was updated
            // Publish the result of the binary operation
            TopicManagerSingleton.get().getTopic(this.output).publish(new Message(this.op.apply(this.num1, this.num2)));
        }
    }

    /**
     * Closes the agent, unsubscribing from input topics and removing itself
     * from the output topic.
     * <p>
     * This method cleans up any subscriptions and publishers related to the
     * agent.
     * </p>
     */
    @Override
    public void close() {
        // Unsubscribe from input topics and remove as a publisher from the output topic
        TopicManagerSingleton.get().getTopic(this.input1).unsubscribe(this);
        TopicManagerSingleton.get().getTopic(this.input2).unsubscribe(this);
        TopicManagerSingleton.get().getTopic(this.output).removePublisher(this);
    }

    /**
     * Compares this {@code BinOpAgent} to another object for equality.
     * <p>
     * Two agents are considered equal if they have the same name, input topics,
     * output topic, and binary operation.
     * </p>
     * 
     * @param obj the object to be compared
     * @return {@code true} if this agent is equal to the specified object, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BinOpAgent other = (BinOpAgent) obj;

        return name.equals(other.name) &&
               input1.equals(other.input1) &&
               input2.equals(other.input2) &&
               output.equals(other.output) &&
               op.equals(other.op);
    }
}