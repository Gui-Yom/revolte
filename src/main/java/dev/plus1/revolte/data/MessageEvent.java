package dev.plus1.revolte.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageEvent extends WebhookEvent {

    private MessageUnit[] messaging;

    @Data
    @NoArgsConstructor
    public class MessageUnit {

        private Person sender;
        private Person recipient;
        private long timestamp;
        private Message message;

        @Data
        @NoArgsConstructor
        public class Message {

            private String mid;
            private String text;
        }

    }
}
