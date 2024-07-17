package graph;

import java.util.Arrays;

public class SortAgent implements Agent {

    private final String name = "Sort";
    private String input;
    private String output;
    private String[] list;
    
    public SortAgent(String[] subs, String[] pubs) {
        this.reset(); // Reset value to 0
        try {
            input = subs[0];
            TopicManagerSingleton.get().getTopic(this.input).subscribe(this); // If exist subscribe to him
        } catch (Exception e) {
            input = null;
            System.out.println("No subs for Agent: " + this.name);;
        }
        try {
            output = pubs[0];
            TopicManagerSingleton.get().getTopic(this.output).addPublisher(this); // If exist publish to him
        } catch (Exception e) {
            output = null;
            System.out.println("No pubs for Agent: " + this.name);;
        }
    }

    @Override
    public String getName() {return this.name;}

    @Override
    public void reset() {
        this.list = new String[0]; // Reset value to 0
    }

    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(this.input)) { // If topic is first publisher
            this.list = msg.asText.split(","); // Update value
            Arrays.sort(this.list);
            System.out.println("Agent: " + this.name + " Changed to: " + this.list);
            if (this.output != null) // Update subscriber
                TopicManagerSingleton.get().getTopic(this.output).publish(new Message(String.join(",", this.list)));
        }
    }

    @Override
    public void close() {
        // Unsubscribe and publish to all topics
        if (this.input != null)
            TopicManagerSingleton.get().getTopic(this.input).unsubscribe(this);
        if (this.output != null)
            TopicManagerSingleton.get().getTopic(this.output).removePublisher(this);
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        SortAgent other = (SortAgent) obj;
        
        return input.equals(other.input) &&
               output.equals(other.output); // Return if same publishers and subscribers
    }
    
}
