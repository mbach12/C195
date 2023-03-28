package Helper;

import java.time.LocalDateTime;

/**
 * Interface to validate that appointment times do not overlap. This interface is used by Lambda expression.
 */
public interface CheckOverlap {
    boolean validate(int customerID, int appointmentID, LocalDateTime start, LocalDateTime end);

}
