package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Represents an agent that wraps another agent and processes callback messages
 * in a separate thread to allow for parallel processing.
 * <p>
 * This agent uses a blocking queue to manage incoming messages and processes them
 * asynchronously in a dedicated thread. The wrapped agent's callbacks are executed
 * in response to messages taken from the queue.
 * </p>
 */
public class ParallelAgent implements Agent {

    /**
     * The wrapped agent whose callbacks are processed in parallel.
     */
    private Agent agent;

    /**
     * The blocking queue used to manage messages for asynchronous processing.
     */
    private BlockingQueue<Message> messageQueue;

    /**
     * Flag indicating whether the parallel processing thread should stop.
     */
    private boolean stop;

    /**
     * Constructs a {@code ParallelAgent} that wraps the given agent and uses a queue
     * with the specified capacity for managing messages.
     * <p>
     * A new thread is started to process messages from the queue and execute the
     * wrapped agent's callbacks.
     * </p>
     * 
     * @param agent the agent to be wrapped and processed in parallel
     * @param capacity the capacity of the blocking queue used for message management
     */
    public ParallelAgent(Agent agent, int capacity) {
        this.agent = agent;
        this.messageQueue = new ArrayBlockingQueue<>(capacity);
        this.stop = false;
        
        // Thread to execute the callbacks
        Thread t = new Thread(() -> {
            while (!this.stop) {
                if (this.messageQueue.size() > 0) { // If there are messages to process
                    try {
                        String topic = this.messageQueue.take().asText; // Take the topic
                        Message msg = this.messageQueue.take(); // Take the message
                        this.agent.callback(topic, msg); // Call the agent's callback
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start(); // Start the processing thread
    }

    /**
     * Puts the given message into the queue and schedules the callback for processing.
     * <p>
     * This method places the topic and message into the queue, where they will be
     * processed by the parallel thread.
     * </p>
     * 
     * @param topic the name of the topic related to the callback
     * @param msg the message to be processed by the callback
     */
    @Override
    public void callback(String topic, Message msg) {
        try {
            this.messageQueue.put(new Message(topic)); // Put the topic into the queue
            this.messageQueue.put(msg); // Put the message into the queue
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the name of the wrapped agent.
     * 
     * @return the name of the wrapped agent
     */
    @Override
    public String getName() {
        return this.agent.getName();
    }

    /**
     * Resets the wrapped agent.
     * <p>
     * This method delegates the reset operation to the wrapped agent.
     * </p>
     */
    @Override
    public void reset() {
        this.agent.reset(); // Call the wrapped agent's reset method
    }

    /**
     * Stops the parallel processing thread and closes the wrapped agent.
     * <p>
     * This method sets the stop flag to true, which will terminate the processing
     * thread, and then calls the wrapped agent's close method.
     * </p>
     */
    @Override
    public void close() {
        this.stop = true; // Stop the processing thread
        this.agent.close(); // Close the wrapped agent
    }

    /**
     * Compares this {@code ParallelAgent} to another object for equality.
     * <p>
     * Two {@code ParallelAgent} instances are considered equal if they wrap the same
     * agent instance.
     * </p>
     * 
     * @param obj the object to be compared
     * @return {@code true} if this agent is equal to the specified object, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ParallelAgent other = (ParallelAgent) obj;
        
        return this.agent.equals(other.agent); // Return if the wrapped agents are equal
    }
}
