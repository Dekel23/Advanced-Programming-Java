package graph;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;

public class TopicManagerSingleton {

    public static class TopicManager {
        
        private static final TopicManager instance = new TopicManager();
        
        private ConcurrentHashMap<String, Topic> nameToTopic;
        private TopicManager() {
        	this.nameToTopic = new ConcurrentHashMap<>();
        }
        
        public Topic getTopic(String name) {
            Topic topic = this.nameToTopic.get(name);
            if (topic == null) {
                topic = new Topic(name);
                Topic existingTopic = this.nameToTopic.putIfAbsent(name, topic);
                if (existingTopic != null) {
                    topic = existingTopic;
                }
            }
            return topic;
        }
        
        public Collection<Topic> getTopics() {
        	return this.nameToTopic.values();
        }
        
        public void clear() {
        	this.nameToTopic.clear();
        }
    }
    
    public static TopicManager get() {
        return TopicManager.instance;
    }
}
