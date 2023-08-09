package cap.s42academy.maintenance.domain.entity;

import cap.s42academy.maintenance.domain.valueobject.MaintenanceStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MaintenanceStatus maintenanceStatus;
    @Column(nullable = false)
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public static Maintenance createNew(LocalDateTime startedAt){
        return Maintenance.builder()
                .maintenanceStatus(MaintenanceStatus.ACTIVE)
                .startedAt(startedAt)
                .build();
    }

    public boolean finish(LocalDateTime finishedAt){
        if (this.maintenanceStatus==MaintenanceStatus.FINISHED){
            return false;
        }
        this.maintenanceStatus = MaintenanceStatus.ACTIVE;
        this.finishedAt = finishedAt;
        return true;
    }
}
