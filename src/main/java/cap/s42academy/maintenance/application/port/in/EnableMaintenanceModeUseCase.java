package cap.s42academy.maintenance.application.port.in;

@FunctionalInterface
public interface EnableMaintenanceModeUseCase {
    void handle(EnableMaintenanceModeCommand command);
}
