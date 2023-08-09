package cap.s42academy.maintenance.application.service;

import cap.s42academy.maintenance.application.port.in.ExistsActiveMaintenanceModeUseCase;
import cap.s42academy.maintenance.application.port.out.ExistsActiveMaintenanceModePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class ExistsActiveMaintenanceModeService implements ExistsActiveMaintenanceModeUseCase {
    private final ExistsActiveMaintenanceModePort existsActiveMaintenanceModePort;

    @Transactional(readOnly = true)
    @Override
    public boolean exists() {
        return existsActiveMaintenanceModePort.existsActive();
    }
}
