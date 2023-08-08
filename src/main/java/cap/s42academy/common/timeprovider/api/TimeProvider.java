package cap.s42academy.common.timeprovider.api;

import java.time.*;


@FunctionalInterface
public interface TimeProvider {

    Instant now();

    default ZonedDateTime zonedDateTimeNow() {
        return ZonedDateTime.ofInstant(now(), ZoneId.systemDefault());
    }

    default LocalDate dateNow() {
        return LocalDate.ofInstant(now(), ZoneId.systemDefault());
    }

    default LocalTime timeNow() {return LocalTime.ofInstant(now(), ZoneId.systemDefault());}

    default LocalDateTime dateTimeNow() {
        return LocalDateTime.ofInstant(now(), ZoneId.systemDefault());
    }

}
