package cap.s42academy.maintenance.adapter.out;

import cap.s42academy.maintenance.application.port.out.SaveMaintenancePort;
import cap.s42academy.maintenance.domain.entity.Maintenance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class SaveMaintenanceAdapter implements SaveMaintenancePort {

    private final MaintenanceRepository maintenanceRepository;

    @Override
    public Long save(Maintenance maintenance) {
        return maintenanceRepository.save(maintenance).getId();
    }
}
