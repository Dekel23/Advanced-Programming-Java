package graph;

import java.util.function.BinaryOperator;

public class BinOpAgent implements Agent{

    private final String name; // For binAgent example we were asked to get the name in the constructor
    private String input1;
    private String input2;
    private String output;
    private double num1;
    private double num2;
    private BinaryOperator<Double> op;

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

        if (update) // Update subscriber
            TopicManagerSingleton.get().getTopic(this.output).publish(new Message(this.op.apply(this.num1, this.num2)));
    }

    @Override
    public void close() {
        // Unsubscribe and publish to all topics
        TopicManagerSingleton.get().getTopic(this.input1).unsubscribe(this);
        TopicManagerSingleton.get().getTopic(this.input2).unsubscribe(this);
        TopicManagerSingleton.get().getTopic(this.output).removePublisher(this);
    }

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
