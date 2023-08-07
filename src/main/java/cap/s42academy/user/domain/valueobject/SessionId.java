package cap.s42academy.user.domain.valueobject;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class SessionId implements Serializable {
    private final UUID value;
}
