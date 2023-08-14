package cap.s42academy.common.exception.api;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ErrorDto(
        Instant time,
        int code,
        String error,
        List<String> messages
) {
}
