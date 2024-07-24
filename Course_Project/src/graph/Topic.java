package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a communication topic that manages subscribers and publishers,
 * and facilitates the publishing of messages.
 * <p>
 * Subscribers are {@code Agent} instances that receive notifications when
 * a new message is published. Publishers are {@code Agent} instances that
 * can send messages to this topic.
 * </p>
 */
public class Topic {
    
    /**
     * The name of the topic.
     */
    public final String name;

    /**
     * List of agents subscribed to this topic.
     */
    private List<Agent> subs = new ArrayList<>();

    /**
     * List of agents that are publishers for this topic.
     */
    private List<Agent> pubs = new ArrayList<>();

    /**
     * The last message published to this topic.
     */
    private Message export;

    /**
     * Constructs a {@code Topic} with the given name.
     * <p>
     * The topic is initialized with an empty message and no subscribers or publishers.
     * </p>
     * 
     * @param name the name of the topic
     */
    Topic(String name) {
        this.name = name;
        this.export = new Message("");
    }

    /**
     * Returns the list of subscribers to this topic.
     * 
     * @return the list of subscribers
     */
    public List<Agent> getSubs() {
        return this.subs;
    }

    /**
     * Returns the list of publishers for this topic.
     * 
     * @return the list of publishers
     */
    public List<Agent> getPubs() {
        return this.pubs;
    }

    /**
     * Returns the last message published to this topic.
     * 
     * @return the last message
     */
    public Message getMessage() {
        return this.export;
    }

    /**
     * Adds an {@code Agent} as a subscriber to this topic.
     * 
     * @param a the agent to be added as a subscriber
     */
    public void subscribe(Agent a) {
        this.subs.add(a);
    }

    /**
     * Removes an {@code Agent} from the list of subscribers.
     * 
     * @param a the agent to be removed from the subscribers
     */
    public void unsubscribe(Agent a) {
        for (int i = this.subs.size() - 1; i > -1; i--) {
            if (this.subs.get(i).equals(a)) {
                this.subs.remove(i);
                break;
            }
        }
    }

    /**
     * Publishes a message to this topic.
     * <p>
     * The message is saved as the last published message and all subscribers
     * are notified by calling their {@code callback} method.
     * </p>
     * 
     * @param m the message to be published
     */
    public void publish(Message m) {
        this.export = m; // Save last message
        System.out.println("Topic: " + this.name + " Changed to: " + this.export.asText);
        for (Agent sub : this.subs) { // Call all subscribers' callbacks
            sub.callback(this.name, m);
        }
    }

    /**
     * Adds an {@code Agent} as a publisher for this topic.
     * 
     * @param a the agent to be added as a publisher
     */
    public void addPublisher(Agent a) {
        this.pubs.add(a);
    }

    /**
     * Removes an {@code Agent} from the list of publishers.
     * 
     * @param a the agent to be removed from the publishers
     */
    public void removePublisher(Agent a) {
        for (int i = this.pubs.size() - 1; i > -1; i--) {
            if (this.pubs.get(i).equals(a)) {
                this.pubs.remove(i);
                break;
            }
        }
    }
}
