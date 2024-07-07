package graph;

import java.util.ArrayList;
import java.util.List;

public class Topic {
	
    public final String name;
    private List<Agent> subs = new ArrayList<>();
    private List<Agent> pubs = new ArrayList<>();
    
    Topic(String name) {
        this.name=name;
    }

    public List<Agent> getSubs() {return this.subs;}
    public List<Agent> getPubs() {return this.pubs;}

    public void subscribe(Agent a) {
    	this.subs.add(a);
    }
    
    public void unsubscribe(Agent a) {
    	this.subs.remove(a);
    }

    public void publish(Message m) {
    	for (Agent sub: this.subs)
    		sub.callback(this.name, m);
    }

    public void addPublisher(Agent a) {
    	this.pubs.add(a);
    }

    public void removePublisher(Agent a) {
    	this.pubs.remove(a);
    }
}