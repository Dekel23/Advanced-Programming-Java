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
        
        // Thread to execute the callback's
        Thread t = new Thread(()-> {
            while (!this.stop) {
                if (this.messageQueue.size() > 0){ // If there is something
                    try {
                        String topic = this.messageQueue.take().asText; // Take first parameters of callback and do it
                        Message msg = this.messageQueue.take();
                        this.agent.callback(topic, msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start(); // Start thread
    }

    @Override
    public void callback(String topic, Message msg) {
        try {
            this.messageQueue.put(new Message(topic)); // Put now callback in the queue
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
        this.agent.reset(); // Call agent's reset
    }

    @Override
    public void close() {
        this.stop = true; // Stops and call agent's close
        this.agent.close();
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ParallelAgent other = (ParallelAgent) obj;
        
        return this.agent.equals(other.agent); // Return if agents are equal
    }
}
