package cap.s42academy.maintenance.application.service;

import cap.s42academy.common.timeprovider.api.TimeProvider;
import cap.s42academy.maintenance.application.port.in.EnableMaintenanceModeCommand;
import cap.s42academy.maintenance.application.port.in.EnableMaintenanceModeUseCase;
import cap.s42academy.maintenance.application.port.out.ExistsActiveMaintenanceModePort;
import cap.s42academy.maintenance.application.port.out.SaveMaintenancePort;
import cap.s42academy.maintenance.domain.entity.Maintenance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class EnableMaintenanceModeService implements EnableMaintenanceModeUseCase {

    private final ExistsActiveMaintenanceModePort existsActiveMaintenanceModePort;
    private final TimeProvider timeProvider;
    private final SaveMaintenancePort saveMaintenancePort;

    @Transactional
    @Override
    public void handle(EnableMaintenanceModeCommand command) {
        if (existsActiveMaintenanceModePort.existsActive()){
            return;
        }
        Maintenance maintenance = Maintenance.createNew(timeProvider.dateTimeNow());
        saveMaintenancePort.save(maintenance);
    }
}
