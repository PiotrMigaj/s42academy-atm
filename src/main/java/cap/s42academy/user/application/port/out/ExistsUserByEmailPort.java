package cap.s42academy.user.application.port.out;

@FunctionalInterface
public interface ExistsUserByEmailPort {
    boolean existsBy(String email);
}
