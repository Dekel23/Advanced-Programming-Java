package graph;

public class IndexAgent implements Agent {

    private final String name = "Index"; // All names Plus
    private String input1;
    private String input2;
    private String output;
    private String[] list;
    private int idx;
    
    public IndexAgent(String[] subs, String[] pubs) {
        this.reset(); // Reset values to 0

        try {
            input1 = subs[0];
            TopicManagerSingleton.get().getTopic(this.input1).subscribe(this); // If exist subscribe to first one
        } catch (Exception e) {
            input1 = null;
            System.out.println("No subs for Agent: " + this.name);;
        }
        try {
            input2 = subs[1];
            TopicManagerSingleton.get().getTopic(this.input2).subscribe(this); // If exist subscribe to second one
        } catch (Exception e) {
            input2 = null;
            System.out.println("Only 1 subs for Agent: " + this.name);;
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
        this.list = new String[0]; // Reset values to 0
        this.idx = -1;
    }

    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(this.input1)){ // If topic is first publisher
            this.list = msg.asText.split(","); // Update first value and the subscriber
        }
        
        if (topic.equals(this.input2)){ // If topic is second publisher
            double num = msg.asDouble;
            if (Double.isNaN(num) || (int)num != num) // If message can't interpret as double and as integer
                return;
            this.idx = (int)num; // Update second value and the subscriber
        }

        System.out.println("Agent: " + this.name + " Changed to: " + this.list + " "+ this.idx);
        
        if (this.output != null) // Update subscriber
            try {
                TopicManagerSingleton.get().getTopic(this.output).publish(new Message(this.list[this.idx]));
            } catch (Exception e) {
                TopicManagerSingleton.get().getTopic(this.output).publish(new Message("error"));
            }
    }

    @Override
    public void close() {
        // Unsubscribe and publish to all topics
        if (this.input1 != null)
            TopicManagerSingleton.get().getTopic(this.input1).unsubscribe(this);
        if (this.output != null)
            TopicManagerSingleton.get().getTopic(this.output).removePublisher(this);
        if (this.input2 != null)
            TopicManagerSingleton.get().getTopic(this.input2).unsubscribe(this);
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        IndexAgent other = (IndexAgent) obj;
        
        return this.input1.equals(other.input1) &&
               this.input2.equals(other.input2) &&
               this.output.equals(other.output); // Return if same publishers and subscribers
    }
}
