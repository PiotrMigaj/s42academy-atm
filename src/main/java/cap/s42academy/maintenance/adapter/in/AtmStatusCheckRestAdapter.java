package cap.s42academy.maintenance.adapter.in;

import cap.s42academy.maintenance.application.port.in.ExistsActiveMaintenanceModeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
class AtmStatusCheckRestAdapter {

    private final ExistsActiveMaintenanceModeUseCase activeMaintenanceModeUseCase;

    @GetMapping("api/v1/maintenance/status-check")
    ResponseEntity<Map<String,Boolean>> statusCheck(){
        boolean result = activeMaintenanceModeUseCase.exists();
        return ResponseEntity.status(HttpStatus.OK).body(
                Map.of("isAtmInMaintenanceMode",result)
        );
    }
}
