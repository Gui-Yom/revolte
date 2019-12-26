package dev.plus1.revolte.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WebhookPost<T> {

    private String object;
    private T[] entry;
}
