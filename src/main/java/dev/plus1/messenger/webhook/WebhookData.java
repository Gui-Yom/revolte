package dev.plus1.messenger.webhook;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookData {

    private String object;
    private List<Event> entry;

    public static JsonDeserializer<WebhookData> getDeserializer() {
        return (json, type, ctx) -> {
            WebhookData data = new WebhookData();
            final JsonObject tree = json.getAsJsonObject();
            if (tree.has("object")) {
                data.setObject(tree.get("object").getAsString());
                List<Event> events = new ArrayList<>();
                if (tree.has("entry")) {
                    tree.getAsJsonArray("entry").forEach(elem -> events.add(ctx.deserialize(elem, Event.class)));
                    data.setEntry(events);
                    return data;
                }
            }
            throw new JsonParseException("Invalid json tree for object !");
        };
    }
}
