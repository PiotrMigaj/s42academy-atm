package cap.s42academy.aspect.maintenance;

import cap.s42academy.maintenance.application.port.in.ExistsActiveMaintenanceModeUseCase;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
class MaintenanceModeAspect {

    static final String ATM_IS_CURRENTLY_IN_MAINTENANCE_MODE = "ATM is currently in maintenance mode!";

    private final ExistsActiveMaintenanceModeUseCase existsActiveMaintenanceModeUseCase;

    @Before("execution(* cap.s42academy.user.adapter.in..*(..))")
    public void beforeUserAdapterInMethodExecution() {
        checkIfAtmIsInMaintenanceMode();
    }

    @Before("execution(* cap.s42academy.account.adapter.in..*(..))")
    public void beforeAccountAdapterInMethodExecution() {
        checkIfAtmIsInMaintenanceMode();
    }

    private void checkIfAtmIsInMaintenanceMode() {
        boolean isAtmInMaintenanceMode = existsActiveMaintenanceModeUseCase.exists();
        if (isAtmInMaintenanceMode){
            throw new MaintenanceModeException(ATM_IS_CURRENTLY_IN_MAINTENANCE_MODE);
        }
    }
}
