package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent {

    private Agent agent;
    private BlockingQueue<Message> messageQueue;
    private boolean stop;

    public ParallelAgent(Agent agent, int capacity) {
        this.agent = agent;
        this.messageQueue = new ArrayBlockingQueue<>(capacity);
        this.stop = false;
        
        Thread t = new Thread(()-> {
            while (!this.stop) {
                if (this.messageQueue.size() > 0){
                    try {
                        String topic = this.messageQueue.take().asText;
                        Message msg = this.messageQueue.take();
                        this.agent.callback(topic, msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    @Override
    public void callback(String topic, Message msg) {
        try {
            this.messageQueue.put(new Message(topic));
            this.messageQueue.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return this.agent.getName();
    }

    @Override
    public void reset() {
        this.agent.reset();
    }

    @Override
    public void close() {
        this.stop = true;
        this.agent.close();
    }
}
