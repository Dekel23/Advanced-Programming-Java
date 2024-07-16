package graph;

public class PlusAgent implements Agent {

    private final String name = "Plus"; // All names Plus
    private String input1;
    private String input2;
    private String output;
    private double num1;
    private double num2;
    
    public PlusAgent(String[] subs, String[] pubs) {
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
        this.num1 = 0; // Reset values to 0
        this.num2 = 0;
    }

    @Override
    public void callback(String topic, Message msg) {
        double num = msg.asDouble;
        boolean update = false;
        if (Double.isNaN(num)) // If message can't interpret as double
            return;

        if (topic.equals(this.input1)){ // If topic is first publisher
            this.num1 = num; // Update first value and the subscriber
            update = true;
        }
        
        if (topic.equals(this.input2)){ // If topic is second publisher
            this.num2 = num; // Update second value and the subscriber
            update = true;
        }

        System.out.println("Agent: " + this.name + " Changed to: " + this.num1 + " "+ this.num2);
        
        if (update && this.output != null) // Update subscriber
            TopicManagerSingleton.get().getTopic(this.output).publish(new Message(this.num1 + this.num2));
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
        
        PlusAgent other = (PlusAgent) obj;
        
        return this.input1.equals(other.input1) &&
               this.input2.equals(other.input2) &&
               this.output.equals(other.output); // Return if same publishers and subscribers
    }
}
