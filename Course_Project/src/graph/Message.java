package graph;

import java.util.Date;

/**
 * Represents a message with various data representations.
 * <p>
 * This class provides multiple constructors to initialize the message using a text representation,
 * byte array, or a double value. It automatically computes additional representations such as
 * byte array and double value from the provided input where applicable.
 * </p>
 */
public class Message {

    /**
     * The textual representation of the message.
     */
    public final String asText;

    /**
     * The byte array representation of the message, derived from {@code asText}.
     */
    public final byte[] data;

    /**
     * The double value representation of the message, parsed from {@code asText}.
     * If {@code asText} cannot be parsed into a double, this will be {@link Double#NaN}.
     */
    public final double asDouble;

    /**
     * The date and time when the message was created.
     */
    public final Date date;

    /**
     * Constructs a {@code Message} with the given text.
     * <p>
     * The message will be initialized with its textual representation and
     * its byte array equivalent. Additionally, an attempt will be made to
     * parse the text as a double. The date of the message will be set to the
     * current date and time.
     * </p>
     * 
     * @param asText the text to initialize the message
     */
    public Message(String asText) {
        this.asText = asText;
        this.data = this.asText.getBytes(); // Set message as bytes
        double temp = Double.NaN; // Set message as Double
        try {
            temp = Double.parseDouble(asText);
        } catch (NumberFormatException e) {}
        this.asDouble = temp;
        this.date = new Date(); // Set date of message
    }

    /**
     * Constructs a {@code Message} from the given byte array.
     * <p>
     * The byte array is converted to a string, which is then used to initialize
     * the message. Other attributes are derived from the converted string.
     * </p>
     * 
     * @param data the byte array to initialize the message
     */
    public Message(byte[] data) {
        this(new String(data)); // Call String constructor
    }

    /**
     * Constructs a {@code Message} with the given double value.
     * <p>
     * The double value is converted to a string, which is then used to initialize
     * the message. Other attributes are derived from the converted string.
     * </p>
     * 
     * @param asDouble the double value to initialize the message
     */
    public Message(double asDouble) {
        this(String.valueOf(asDouble)); // Call String constructor
    }
}