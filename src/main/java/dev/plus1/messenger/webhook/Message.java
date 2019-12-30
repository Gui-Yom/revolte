package dev.plus1.messenger.webhook;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String mid;
    private String text;
    private List<Attachment> attachments;
    private String quickReplyPayload;
    private String replyMid;
    private long stickerId;

    public static JsonDeserializer<Message> getDeserializer() {
        return (json, type, ctx) -> {
            Message data = new Message();
            final JsonObject tree = json.getAsJsonObject();

            data.setMid(tree.get("mid").getAsString());

            if (tree.has("text"))
                data.setText(tree.get("text").getAsString());

            if (tree.has("reply_to"))
                data.setReplyMid(tree.get("reply_to").getAsJsonObject().get("mid").getAsString());
            else if (tree.has("quick_reply"))
                data.setQuickReplyPayload(tree.get("quick_reply").getAsJsonObject().get("payload").getAsString());
            else if (tree.has("attachments")) {
                List<Attachment> attachments = new ArrayList<>();
                tree.get("attachments").getAsJsonArray().forEach(elem -> ctx.deserialize(elem, Attachment.class));
                data.setAttachments(attachments);
            }

            if (tree.has("sticker_id"))
                data.setStickerId(tree.get("sticker_id").getAsLong());

            return data;
        };
    }

    public boolean isQuickReply() {
        return quickReplyPayload != null;
    }

    public boolean isReply() {
        return replyMid != null;
    }

    public boolean hasAttachments() {
        return attachments != null;
    }

    public boolean hasText() {
        return text != null;
    }
}
