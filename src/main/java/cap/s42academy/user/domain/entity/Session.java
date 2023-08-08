package cap.s42academy.user.domain.entity;

import cap.s42academy.user.domain.valueobject.SessionId;
import cap.s42academy.user.domain.valueobject.SessionStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_session")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Session {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id",columnDefinition = "BINARY(16)"))
    private SessionId sessionId;
    @Version
    private Long version;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private SessionStatus sessionStatus;
    private LocalDateTime createdAt;
    @Setter
    private LocalDateTime closedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
