package graph;

import java.util.Date;

public class Message {

    public final String asText;
    public final byte[] data;
    public final double asDouble;
    public final Date date;

    public Message(String asText) {
        this.asText = asText;
        this.data = this.asText.getBytes(); // Set message as bytes
        double temp = Double.NaN; // Set message as Double
        try {
        	temp = Double.parseDouble(asText);
        } catch (NumberFormatException e) {}
        this.asDouble = temp;
        this.date = new Date(); // Set date of massage
    }

    public Message(byte[] data) {
        this(new String(data)); // Call String constructor
    }

    public Message(double asDouble) {
        this(String.valueOf(asDouble)); // Call String constructor
    }
}