package dev.plus1.revolte.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WebhookEvent {

    private String id;
    private long time;
}
