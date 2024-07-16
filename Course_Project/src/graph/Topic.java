package graph;

import java.util.ArrayList;
import java.util.List;

public class Topic {
	
    public final String name; // Name
    private List<Agent> subs = new ArrayList<>(); // Subscribers: Agents that listen
    private List<Agent> pubs = new ArrayList<>(); // Publishers: Agents to listen 
    private Message export; // Last message

    Topic(String name) {
        this.name=name;
        this.export = new Message("");
    }

    public List<Agent> getSubs() {return this.subs;}
    public List<Agent> getPubs() {return this.pubs;}
    public Message getMessage() {return this.export;}

    public void subscribe(Agent a) {
    	this.subs.add(a);
    }
    
    public void unsubscribe(Agent a) {
    	for (int i = this.subs.size()-1; i > -1; i--) {
            if (this.subs.get(i).equals(a)) {
                this.subs.remove(i);
                break;
            }
        }
    }

    public void publish(Message m) {
        this.export = m; // Save last message
        System.out.println("Topic: " + this.name + " Changed to: " + this.export.asText);
    	for (Agent sub: this.subs) // Call all subsribers callback's
    		sub.callback(this.name, m);
    }

    public void addPublisher(Agent a) {
    	this.pubs.add(a);
    }

    public void removePublisher(Agent a) {
        for (int i = this.pubs.size()-1; i > -1; i--) {
            if (this.pubs.get(i).equals(a)) {
                this.pubs.remove(i);
                break;
            }
        }
    }
}