package cap.s42academy.common.exception.internal;

import cap.s42academy.aspect.maintenance.api.MaintenanceModeException;
import cap.s42academy.common.exception.api.ErrorDto;
import cap.s42academy.common.exception.api.UserUnauthenticatedException;
import cap.s42academy.common.timeprovider.api.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
class GlobalExceptionHandler {

    private final TimeProvider timeProvider;

    @ExceptionHandler(MaintenanceModeException.class)
    ResponseEntity<ErrorDto> handleException(MaintenanceModeException maintenanceModeException) {
        HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
        String message = maintenanceModeException.getMessage()==null? "":maintenanceModeException.getMessage();
        return ResponseEntity.status(httpStatus).body(createErrorDto(List.of(message),httpStatus));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorDto> handleException(IllegalArgumentException illegalArgumentException) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = illegalArgumentException.getMessage()==null? "":illegalArgumentException.getMessage();
        return ResponseEntity.status(httpStatus).body(createErrorDto(List.of(message),httpStatus));
    }

    @ExceptionHandler(UserUnauthenticatedException.class)
    ResponseEntity<ErrorDto> handleException(UserUnauthenticatedException userUnauthenticatedException) {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        String message = userUnauthenticatedException.getMessage()==null? "":userUnauthenticatedException.getMessage();
        return ResponseEntity.status(httpStatus).body(createErrorDto(List.of(message),httpStatus));
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<ErrorDto> handleException(IllegalStateException illegalStateException) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = illegalStateException.getMessage()==null? "":illegalStateException.getMessage();
        return ResponseEntity.status(httpStatus).body(createErrorDto(List.of(message),httpStatus));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<ErrorDto> handleException(EntityNotFoundException entityNotFoundException) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        String message = entityNotFoundException.getMessage()==null? "":entityNotFoundException.getMessage();
        return ResponseEntity.status(httpStatus).body(createErrorDto(List.of(message),httpStatus));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorDto> handleException(Exception exception) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Ups... Something went wrong, we are trying to fix it!";
        return ResponseEntity.status(httpStatus).body(createErrorDto(List.of(message),httpStatus));
    }

    @ExceptionHandler(ValidationException.class)
    ResponseEntity<ErrorDto> handleException(ValidationException validationException){
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ErrorDto errorDto;
        if (validationException instanceof ConstraintViolationException){
            List<String> messages = extractViolationsFromException((ConstraintViolationException) validationException);
            errorDto = createErrorDto(messages,httpStatus);
        }else {
            String exceptionMessage = validationException.getMessage();
            errorDto = createErrorDto(List.of(exceptionMessage),httpStatus);
        }
        return ResponseEntity.status(httpStatus).body(errorDto);
    }
    private List<String> extractViolationsFromException(ConstraintViolationException validationException) {
        return validationException.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();
    }
    private ErrorDto createErrorDto(List<String> messages, HttpStatus httpStatus) {
        return ErrorDto.builder()
                .time(timeProvider.now())
                .code(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .messages(messages)
                .build();
    }
}
