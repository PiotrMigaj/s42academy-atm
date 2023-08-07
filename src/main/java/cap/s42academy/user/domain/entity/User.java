package cap.s42academy.user.domain.entity;

import cap.s42academy.user.domain.valueobject.UserId;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id",columnDefinition = "BINARY(16)"))
    private UserId userId;
    @Version
    private Long version;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false,columnDefinition = "CHAR(60)")
    private String pin;
}
