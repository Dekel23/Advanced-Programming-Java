package graph;

import java.util.Date;

public class Message {

    public final String asText;
    public final byte[] data;
    public final double asDouble;
    public final Date date;

    public Message(String asText) {
        this.asText = asText;
        this.data = this.asText.getBytes();
        double temp = Double.NaN;
        try {
        	temp = Double.parseDouble(asText);
        } catch (NumberFormatException e) {}
        this.asDouble = temp;
        this.date = new Date();
    }

    public Message(byte[] data) {
        this(new String(data));
    }

    public Message(double asDouble) {
        this(String.valueOf(asDouble));
    }
}