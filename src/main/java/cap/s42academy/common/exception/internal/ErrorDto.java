package cap.s42academy.common.exception.internal;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
record ErrorDto(
        Instant time,
        int code,
        String error,
        List<String> messages
) {
}
