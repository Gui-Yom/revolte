package dev.plus1.messenger.sendapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericTemplateMessage {
    private String recipientId;
    private String title;
    private String imageUrl;
    private String subtitle;
    private String webviewUrl;
    private String buttonText;
    private String webviewSize;

    public static JsonSerializer<GenericTemplateMessage> getSerializer() {
        return (obj, type, ctx) -> {
            JsonObject data = new JsonObject();
            JsonObject recipient = new JsonObject();
            recipient.addProperty("id", obj.recipientId);
            data.add("recipient", recipient);
            JsonObject message = new JsonObject();
            JsonObject attachment = new JsonObject();
            attachment.addProperty("type", "template");
            JsonObject payload = new JsonObject();
            payload.addProperty("template_type", "generic");
            JsonArray elements = new JsonArray();
            JsonObject element = new JsonObject();
            element.addProperty("title", obj.title);
            element.addProperty("image_url", obj.imageUrl);
            element.addProperty("subtitle", obj.subtitle);
            JsonObject defaultAction = new JsonObject();
            defaultAction.addProperty("type", "webview_url");
            defaultAction.addProperty("url", obj.webviewUrl);
            defaultAction.addProperty("messenger_extensions", true);
            defaultAction.addProperty("webview_height_ratio", obj.webviewSize);
            element.add("default_action", defaultAction);
            JsonArray buttons = new JsonArray();
            JsonObject button = new JsonObject();
            button.addProperty("type", "web_url");
            button.addProperty("url", obj.webviewUrl);
            button.addProperty("title", obj.buttonText);
            buttons.add(button);
            element.add("buttons", buttons);
            elements.add(element);
            payload.add("elements", elements);
            attachment.add("payload", payload);
            message.add("attachment", attachment);
            data.add("message", message);
            return data;
        };
    }
}
