package Helper;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Interface to convert date/time. This interface is used by Lambda expression.
 */
public interface ConvertDT {
    LocalDateTime convertDT(Timestamp dateTime);

}
