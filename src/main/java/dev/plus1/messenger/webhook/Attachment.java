package dev.plus1.messenger.webhook;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    private String type;
    private String payload;
    private String title;
    private URL url;
    private String coordinates;

    public static JsonDeserializer<Attachment> getDeserializer() {
        return (json, type, ctx) -> {
            Attachment data = new Attachment();
            final JsonObject tree = json.getAsJsonObject();

            data.setType(tree.get("type").getAsString());

            if (data.isLocation())
                data.setCoordinates(ctx.deserialize(tree.get("coordinates"), Coordinates.class));
            else if (data.isFallback()) {
                data.setTitle(tree.get("title").getAsString());
                data.setUrl(ctx.deserialize(tree.get("url"), URL.class));
            } else if (data.isDocument())
                data.setUrl(ctx.deserialize(tree.get("payload").getAsJsonObject().get("url"), URL.class));
            else
                data.setPayload(tree.get("paylaod").getAsString());

            return data;
        };
    }

    public boolean isFile() {
        return type.equals("file");
    }

    public boolean isAudio() {
        return type.equals("audio");
    }

    public boolean isImage() {
        return type.equals("image");
    }

    public boolean isVideo() {
        return type.equals("video");
    }

    public boolean isFallback() {
        return type.equals("fallback");
    }

    public boolean isTemplate() {
        return type.equals("template");
    }

    public boolean isLocation() {
        return type.equals("location");
    }

    public boolean isDocument() {
        return isAudio() || isFile() || isImage() || isVideo();
    }
}
