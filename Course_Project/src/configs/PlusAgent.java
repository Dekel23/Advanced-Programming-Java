package configs;

import graph.Agent;
import graph.TopicManagerSingleton;
import graph.Message;

public class PlusAgent implements Agent {

    private static int instanceCount = 0;
    private String name;
    private String input1;
    private String input2;
    private String output;
    private double num1;
    private double num2;
    
    public PlusAgent(String[] subs, String[] pubs) {
        this.name = "Plus" + Integer.toString(instanceCount++);
        this.reset();
        try {
            input1 = subs[0];
            TopicManagerSingleton.get().getTopic(this.input1).subscribe(this);
        } catch (Exception e) {
            input1 = null;
            System.out.println("No subs for Agent: " + this.name);;
        }
        try {
            input2 = subs[1];
            TopicManagerSingleton.get().getTopic(this.input2).subscribe(this);
        } catch (Exception e) {
            input2 = null;
            System.out.println("Only 1 subs for Agent: " + this.name);;
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
        this.num1 = 0;
        this.num2 = 0;
    }

    @Override
    public void callback(String topic, Message msg) {
        double num = msg.asDouble;
        boolean update = false;
        if (Double.isNaN(num))
            return;
        
        if (topic.equals(this.input1))
            this.num1 = num;
            update = true;

        if (topic.equals(this.input2))
            this.num2 = num;
            update = true;
        
        if (update && this.output != null)
            TopicManagerSingleton.get().getTopic(this.output).publish(new Message(this.num1 + this.num2));
    }

    @Override
    public void close() {
    }
    
}
