package dev.plus1.messenger.webhook;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Postback {

    private String title;
    private String payload;
    private String ref;
    private String source;
    private String type;

    public static JsonDeserializer<Postback> getDeserializer() {
        return (json, type, ctx) -> {
            Postback data = new Postback();
            final JsonObject tree = json.getAsJsonObject();

            data.setTitle(tree.get("title").getAsString());
            data.setPayload(tree.get("payload").getAsString());
            if (tree.has("referral")) {
                data.setRef(tree.get("referral").getAsJsonObject().get("ref").getAsString());
                data.setSource(tree.get("referral").getAsJsonObject().get("source").getAsString());
                data.setType(tree.get("referral").getAsJsonObject().get("type").getAsString());
            }

            return data;
        };
    }
}
