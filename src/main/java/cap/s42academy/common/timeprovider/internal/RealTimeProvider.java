package cap.s42academy.common.timeprovider.internal;

import cap.s42academy.common.timeprovider.api.TimeProvider;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
class RealTimeProvider implements TimeProvider {

    @Override
    public Instant now() {
        return Instant.now();
    }

}
