package cap.s42academy.maintenance.adapter.in;

import cap.s42academy.maintenance.application.port.in.EnableMaintenanceModeCommand;
import cap.s42academy.maintenance.application.port.in.EnableMaintenanceModeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class EnableMaintenanceModeRestAdapter {

    private final EnableMaintenanceModeUseCase enableMaintenanceModeUseCase;

    @PostMapping("api/v1/enable-maintenance-mode")
    ResponseEntity<Void> enableMaintenanceMode(){
        enableMaintenanceModeUseCase.handle(new EnableMaintenanceModeCommand());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
