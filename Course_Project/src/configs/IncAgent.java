package configs;

import graph.Agent;
import graph.TopicManagerSingleton;
import graph.Message;

public class IncAgent implements Agent {

    private static int instanceCount = 0;
    private String name;
    private String input;
    private String output;
    private double num;
    
    public IncAgent(String[] subs, String[] pubs) {
        this.name = "Inc" + Integer.toString(instanceCount++);
        this.reset();
        try {
            input = subs[0];
            TopicManagerSingleton.get().getTopic(this.input).subscribe(this);
        } catch (Exception e) {
            input = null;
            System.out.println("No subs for Agent: " + this.name);;
        }
        try {
            output = pubs[0];
            TopicManagerSingleton.get().getTopic(this.output).addPublisher(this);
        } catch (Exception e) {
            output = null;
            System.out.println("No pubs for Agent: " + this.name);;
        }
    }

    @Override
    public String getName() {return this.name;}

    @Override
    public void reset() {
        this.num = 0;
    }

    @Override
    public void callback(String topic, Message msg) {
        double num = msg.asDouble;

        if (Double.isNaN(num))
            return;
        
        if (topic.equals(this.input)) {
            this.num = num;
            if (this.output != null)
                TopicManagerSingleton.get().getTopic(this.output).publish(new Message(this.num + 1));
        }
    }

    @Override
    public void close() {
    }
    
}
