package cap.s42academy.maintenance.adapter.out;

import cap.s42academy.maintenance.application.port.out.ExistsActiveMaintenanceModePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class ExistsActiveMaintenanceModeAdapter implements ExistsActiveMaintenanceModePort {

    private final MaintenanceRepository maintenanceRepository;
    @Override
    public boolean existsActive() {
        return maintenanceRepository.existsActiveMaintenanceMode();
    }
}
