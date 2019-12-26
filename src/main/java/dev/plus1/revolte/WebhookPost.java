package dev.plus1.revolte;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WebhookPost {

    private String object;
    private WebhookEvent[] entry;
}
