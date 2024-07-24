package graph;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;

/**
 * Manages the creation and retrieval of {@code Topic} instances in a singleton pattern.
 * <p>
 * This class provides a thread-safe way to access and manage topics using a singleton
 * instance of {@code TopicManager}. The topics are stored in a concurrent hash map where
 * the key is the topic name and the value is the {@code Topic} instance.
 * </p>
 * <p>
 * This class does not explicitly define a constructor. The default constructor is used
 * to initialize the class. The singleton instance is provided through the static
 * {@link #get()} method.
 * </p>
 */
public class TopicManagerSingleton {

    /**
     * Singleton class for managing topics.
     * <p>
     * This class ensures that only one instance of the topic manager exists
     * and provides methods to retrieve or create topics.
     * </p>
     */
    public static class TopicManager {

        /**
         * The single instance of {@code TopicManager}.
         */
        private static final TopicManager instance = new TopicManager();

        /**
         * A concurrent hash map storing topics with their names as keys.
         */
        private ConcurrentHashMap<String, Topic> nameToTopic;

        /**
         * Private constructor to prevent instantiation from outside the class.
         * <p>
         * Initializes the concurrent hash map that stores topics. This constructor is private
         * to ensure that only one instance of {@code TopicManager} is created and used throughout
         * the application, maintaining the singleton pattern.
         * </p>
         */
        private TopicManager() {
            this.nameToTopic = new ConcurrentHashMap<>();
        }

        /**
         * Retrieves a {@code Topic} by its name, creating a new one if it does not exist.
         * <p>
         * If the topic does not already exist, a new {@code Topic} instance is created
         * and added to the map. If another thread adds the topic before this thread, the
         * existing topic is returned instead.
         * </p>
         * 
         * @param name the name of the topic to retrieve or create
         * @return the {@code Topic} instance associated with the given name
         */
        public Topic getTopic(String name) {
            Topic topic = this.nameToTopic.get(name); // Return if exists
            if (topic == null) {
                topic = new Topic(name); // Create new topic
                Topic existingTopic = this.nameToTopic.putIfAbsent(name, topic); // Put if doesn't exist
                if (existingTopic != null) {
                    topic = existingTopic; // If exists, set to the existing one
                }
            }
            return topic;
        }

        /**
         * Returns a collection of all topics managed by this {@code TopicManager}.
         * 
         * @return a collection of {@code Topic} instances
         */
        public Collection<Topic> getTopics() {
            return this.nameToTopic.values();
        }

        /**
         * Clears all topics from the manager.
         * <p>
         * This method removes all topics from the manager, effectively resetting
         * the state of the topic manager.
         * </p>
         */
        public void clear() {
            this.nameToTopic.clear();
        }
    }

    /**
     * Returns the singleton instance of {@code TopicManager}.
     * 
     * @return the singleton {@code TopicManager} instance
     */
    public static TopicManager get() {
        return TopicManager.instance;
    }
}