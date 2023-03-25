package Helper;

import java.time.Instant;

/**
 * Interface to convert and check date/time. This interface is used by Lambda expression.
 */
public interface CheckDT {
    boolean validate(Instant instant);

}
