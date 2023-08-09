package cap.s42academy.maintenance.application.port.out;

import cap.s42academy.maintenance.domain.entity.Maintenance;

@FunctionalInterface
public interface SaveMaintenancePort {
    Long save(Maintenance maintenance);
}
