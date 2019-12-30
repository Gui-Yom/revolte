package dev.plus1.messenger.webhook;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private String id;
    private long time;
    private Person sender;
    private Person recipient;
    private long timestamp;
    private Message message;
    private Postback postback;
    private EventType eventType;

    public static JsonDeserializer<Event> getDeserializer() {
        return (json, type, ctx) -> {
            Event data = new Event();
            final JsonObject tree = json.getAsJsonObject();
            data.setId(tree.get("id").getAsString());
            data.setTime(tree.get("time").getAsLong());
            final JsonObject messaging = tree.get("messaging").getAsJsonArray().get(0).getAsJsonObject();
            data.setSender(ctx.deserialize(messaging.get("sender"), Person.class));
            data.setRecipient(ctx.deserialize(messaging.get("recipient"), Person.class));
            data.setTimestamp(messaging.get("timestamp").getAsLong());
            if (tree.has("message")) {
                data.setMessage(ctx.deserialize(tree.get("message"), Message.class));
                data.setEventType(EventType.MESSAGES);
            } else if (tree.has("postback")) {
                data.setPostback(ctx.deserialize(tree.get("postback"), Postback.class));
                data.setEventType(EventType.POSTBACK);
            }
            return data;
        };
    }

    public enum EventType {
        MESSAGES,
        POSTBACK
    }
}
