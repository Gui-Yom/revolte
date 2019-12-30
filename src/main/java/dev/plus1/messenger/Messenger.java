package dev.plus1.messenger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.plus1.messenger.webhook.*;

import java.util.List;

public final class Messenger {

    private static Gson gson;

    static {
        gson = new GsonBuilder()
                       .registerTypeAdapter(WebhookData.class, WebhookData.getDeserializer())
                       .registerTypeAdapter(Event.class, Event.getDeserializer())
                       .registerTypeAdapter(Message.class, Message.getDeserializer())
                       .registerTypeAdapter(Postback.class, Postback.getDeserializer())
                       .registerTypeAdapter(Attachment.class, Attachment.getDeserializer())
                       .create();
    }

    public static List<Event> handleWebhook(String payload) {

        return gson.fromJson(payload, WebhookData.class).getEntry();
    }
}
