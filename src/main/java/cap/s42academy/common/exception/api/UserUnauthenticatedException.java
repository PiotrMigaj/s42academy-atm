package cap.s42academy.common.exception.api;

public class UserUnauthenticatedException extends RuntimeException{
    public UserUnauthenticatedException(String message) {
        super(message);
    }
}
