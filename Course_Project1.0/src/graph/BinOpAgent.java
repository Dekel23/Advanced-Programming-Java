package graph;

import java.util.function.BinaryOperator;

public class BinOpAgent implements Agent{

    private String name;
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
        this.num1 = Double.NaN;
        this.num2 = Double.NaN;
        TopicManagerSingleton.get().getTopic(this.input1).subscribe(this);
        TopicManagerSingleton.get().getTopic(this.input2).subscribe(this);
        TopicManagerSingleton.get().getTopic(this.output).addPublisher(this);
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
        if (Double.isNaN(num))
            return;

        if (topic.equals(this.input1))
            num1 = num;

        if (topic.equals(this.input2))
            num2 = num;

        if (Double.isNaN(this.num1) || Double.isNaN(this.num2))
            return;

        TopicManagerSingleton.get().getTopic(this.output).publish(new Message(this.op.apply(this.num1, this.num2)));
    }

    @Override
    public void close() {
    }
    
}
