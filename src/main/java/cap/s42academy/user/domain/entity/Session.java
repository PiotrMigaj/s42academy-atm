package cap.s42academy.user.domain.entity;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long version;
    @Column(nullable = false)
    private SessionStatus sessionStatus;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
