package dev.plus1.revolte.data;

import dev.plus1.revolte.Phase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewGame {
    private String threadId;
    private Map<Phase, Duration> phasesDuration;
    private String developperKey;
}
