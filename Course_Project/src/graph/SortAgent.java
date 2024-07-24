package graph;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Represents an agent that sorts a list of numeric values received from an input topic
 * and publishes the sorted list to an output topic.
 * <p>
 * The {@code SortAgent} subscribes to an input topic to receive a comma-separated list of numeric values,
 * sorts these values in ascending order, and publishes the sorted list to an output topic.
 * </p>
 */
public class SortAgent implements Agent {

    /**
     * The name of this agent.
     */
    private final String name = "Sort";

    /**
     * The input topic from which a list of numeric values is received.
     */
    private String input;

    /**
     * The output topic to which the sorted list is published.
     */
    private String output;

    /**
     * The list of numeric values to be sorted.
     */
    private String[] list;
    
    /**
     * Constructs a {@code SortAgent} that subscribes to the specified input topic
     * and publishes the sorted list to the specified output topic.
     * <p>
     * The agent subscribes to the input topic specified in the {@code subs} array and
     * publishes the sorted list to the output topic specified in the {@code pubs} array.
     * </p>
     * 
     * @param subs an array containing the names of the input topics
     * @param pubs an array containing the names of the output topics
     */
    public SortAgent(String[] subs, String[] pubs) {
        this.reset(); // Reset the list to empty
        try {
            input = subs[0];
            TopicManagerSingleton.get().getTopic(this.input).subscribe(this); // Subscribe to the input topic
        } catch (Exception e) {
            input = null;
            System.out.println("No subs for Agent: " + this.name);
        }
        try {
            output = pubs[0];
            TopicManagerSingleton.get().getTopic(this.output).addPublisher(this); // Add this agent as a publisher to the output topic
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
     * Resets the list to an empty array.
     * <p>
     * This method initializes {@code list} to an empty array.
     * </p>
     */
    @Override
    public void reset() {
        this.list = new String[0];
    }

    /**
     * Processes a callback by sorting the list of numeric values and publishing the sorted list.
     * <p>
     * If the topic corresponds to {@code input}, the list is updated with the new values. The values are then
     * parsed, sorted in ascending order, and published to the {@code output} topic.
     * </p>
     * 
     * @param topic the name of the topic from which the message was received
     * @param msg the message containing the comma-separated list of numeric values
     */
    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(this.input)) { // If the topic is the input topic
            this.list = msg.asText.split(","); // Update the list with new values
            for (String str: this.list){
                try {
                    double num = Double.parseDouble(str);
                    if (Double.isNaN(num))
                        return;
                } catch (NumberFormatException e) {
                    return;
                }
            }
            Arrays.sort(this.list, new Comparator<String>() { // Sort the list
                public int compare(String s1, String s2) {
                    return Double.compare(Double.parseDouble(s1), Double.parseDouble(s2));
                }
            });
            System.out.println("Agent: " + this.name + " Changed to: " + Arrays.toString(this.list));
            if (this.output != null) // Publish the sorted list
                TopicManagerSingleton.get().getTopic(this.output).publish(new Message(String.join(",", this.list)));
        }
    }

    /**
     * Unsubscribes from the input topic and removes this agent as a publisher from the output topic.
     * <p>
     * This method ensures that the agent no longer receives updates or sends messages
     * to the topics it was associated with.
     * </p>
     */
    @Override
    public void close() {
        if (this.input != null)
            TopicManagerSingleton.get().getTopic(this.input).unsubscribe(this);
        if (this.output != null)
            TopicManagerSingleton.get().getTopic(this.output).removePublisher(this);
    }

    /**
     * Compares this {@code SortAgent} to another object for equality.
     * <p>
     * Two {@code SortAgent} instances are considered equal if they have the same input topic
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
        
        SortAgent other = (SortAgent) obj;
        
        return input.equals(other.input) &&
               output.equals(other.output);
    }
}