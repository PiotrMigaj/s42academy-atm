package cap.s42academy.account.adapter.out;

import cap.s42academy.account.application.port.out.AtmMaintenanceModePort;
import cap.s42academy.maintenance.application.port.in.ExistsActiveMaintenanceModeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AtmMaintenanceModeAdapter implements AtmMaintenanceModePort {

    private final ExistsActiveMaintenanceModeUseCase existsActiveMaintenanceModeUseCase;

    @Override
    public boolean exists() {
        return existsActiveMaintenanceModeUseCase.exists();
    }
}
