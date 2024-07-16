package graph;

public class IncAgent implements Agent {

    private final String name = "Inc";
    private String input;
    private String output;
    private double num;
    
    public IncAgent(String[] subs, String[] pubs) {
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
        this.num = 0; // Reset value to 0
    }

    @Override
    public void callback(String topic, Message msg) {
        double num = msg.asDouble;

        if (Double.isNaN(num)) // If message can't interpret as double
            return;
        
        if (topic.equals(this.input)) { // If topic is first publisher
            this.num = num; // Update value
            System.out.println("Agent: " + this.name + " Changed to: " + this.num);
            if (this.output != null) // Update subscriber
                TopicManagerSingleton.get().getTopic(this.output).publish(new Message(this.num + 1));
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
        
        IncAgent other = (IncAgent) obj;
        
        return input.equals(other.input) &&
               output.equals(other.output); // Return if same publishers and subscribers
    }
    
}
