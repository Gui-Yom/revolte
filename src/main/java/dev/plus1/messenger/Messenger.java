package dev.plus1.messenger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.plus1.messenger.webhook.*;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.List;

public final class Messenger {

    private static final Gson gson;
    private static final String PAGE_ACCESS_TOKEN = System.getenv("PAGE_ACCESS_TOKEN");
    private static final OkHttpClient http = new OkHttpClient();

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

    public static boolean postSenderAction(SenderAction action, Person recipient) {

        try {
            http.newCall(new Request.Builder()
                                 .url("https://graph.facebook.com/v5.0/me/messages?access_token=" + PAGE_ACCESS_TOKEN)
                                 .post(RequestBody.create(getSenderAction(action, recipient),
                                         MediaType.get("application/json; charset=utf-8")))
                                 .build())
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String getSenderAction(SenderAction action, Person recipient) {

        final JsonObject object = new JsonObject();
        object.add("recipient", gson.toJsonTree(recipient));
        object.add("sender_action", new JsonPrimitive(action.name().toLowerCase()));
        return gson.toJson(object);
    }

    public enum SenderAction {
        TYPING_ON,
        TYPING_OFF,
        MARK_SEEN
    }
}
