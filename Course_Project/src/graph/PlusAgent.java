package graph;

/**
 * Represents an agent that performs addition on values received from two input topics
 * and publishes the result to an output topic.
 * <p>
 * The {@code PlusAgent} subscribes to two input topics to receive numeric values,
 * adds these values, and publishes the result to an output topic.
 * </p>
 */
public class PlusAgent implements Agent {

    /**
     * The name of this agent.
     */
    private final String name = "Plus";

    /**
     * The first input topic from which numeric values are received.
     */
    private String input1;

    /**
     * The second input topic from which numeric values are received.
     */
    private String input2;

    /**
     * The output topic to which the result of the addition is published.
     */
    private String output;

    /**
     * The numeric value received from the first input topic.
     */
    private double num1;

    /**
     * The numeric value received from the second input topic.
     */
    private double num2;

    /**
     * Constructs a {@code PlusAgent} that subscribes to the specified input topics
     * and publishes the result to the specified output topic.
     * <p>
     * The agent subscribes to the first two topics in the {@code subs} array and
     * publishes the result to the topic in the {@code pubs} array.
     * </p>
     * 
     * @param subs an array containing the names of the input topics
     * @param pubs an array containing the names of the output topics
     */
    public PlusAgent(String[] subs, String[] pubs) {
        this.reset(); // Reset values to 0

        try {
            input1 = subs[0];
            TopicManagerSingleton.get().getTopic(this.input1).subscribe(this); // Subscribe to the first input topic
        } catch (Exception e) {
            input1 = null;
            System.out.println("No subs for Agent: " + this.name);
        }
        try {
            input2 = subs[1];
            TopicManagerSingleton.get().getTopic(this.input2).subscribe(this); // Subscribe to the second input topic
        } catch (Exception e) {
            input2 = null;
            System.out.println("Only 1 subs for Agent: " + this.name);
        }
        try {
            output = pubs[0];
            TopicManagerSingleton.get().getTopic(this.output).addPublisher(this); // Publish the result to the output topic
        } catch (Exception e) {
            output = null;
            System.out.println("No pubs for Agent: " + this.name);
        }
    }

    /**
     * Returns the name of this agent.
     * 
     * @return the name of this agent
     */
    @Override
    public String getName() {return this.name;}

    /**
     * Resets the numeric values used for addition to 0.
     * <p>
     * This method initializes {@code num1} and {@code num2} to 0.
     * </p>
     */
    @Override
    public void reset() {
        this.num1 = 0;
        this.num2 = 0;
    }

    /**
     * Processes a callback by updating the numeric values based on the topic and message,
     * and publishes the result of the addition to the output topic.
     * <p>
     * If the topic corresponds to {@code input1} or {@code input2}, the respective numeric
     * value is updated. If both input values have been received, the sum is calculated and
     * published to the {@code output} topic.
     * </p>
     * 
     * @param topic the name of the topic from which the message was received
     * @param msg the message containing the numeric value
     */
    @Override
    public void callback(String topic, Message msg) {
        double num = msg.asDouble;
        boolean update = false;
        if (Double.isNaN(num)) // If message can't be interpreted as double
            return;

        if (topic.equals(this.input1)){ // If topic is the first input
            this.num1 = num; // Update the first value
            update = true;
        }
        
        if (topic.equals(this.input2)){ // If topic is the second input
            this.num2 = num; // Update the second value
            update = true;
        }

        System.out.println("Agent: " + this.name + " Changed to: " + this.num1 + " " + this.num2);
        
        if (update && this.output != null) // Publish the result if both inputs are updated
            TopicManagerSingleton.get().getTopic(this.output).publish(new Message(this.num1 + this.num2));
    }

    /**
     * Unsubscribes from all input topics and removes this agent as a publisher from the output topic.
     * <p>
     * This method ensures that the agent no longer receives updates or sends messages
     * to the topics it was associated with.
     * </p>
     */
    @Override
    public void close() {
        if (this.input1 != null)
            TopicManagerSingleton.get().getTopic(this.input1).unsubscribe(this);
        if (this.output != null)
            TopicManagerSingleton.get().getTopic(this.output).removePublisher(this);
        if (this.input2 != null)
            TopicManagerSingleton.get().getTopic(this.input2).unsubscribe(this);
    }

    /**
     * Compares this {@code PlusAgent} to another object for equality.
     * <p>
     * Two {@code PlusAgent} instances are considered equal if they have the same input topics
     * and output topic.
     * </p>
     * 
     * @param obj the object to be compared
     * @return {@code true} if this agent is equal to the specified object, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        PlusAgent other = (PlusAgent) obj;
        
        return this.input1.equals(other.input1) &&
               this.input2.equals(other.input2) &&
               this.output.equals(other.output);
    }
}