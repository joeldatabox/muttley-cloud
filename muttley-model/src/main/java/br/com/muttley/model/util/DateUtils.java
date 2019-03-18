package br.com.muttley.model.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 18/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class DateUtils {
    public static LocalDateTime toLocalDateTime(final Date date) {
        return toLocalDateTime(date, ZoneId.systemDefault());
    }

    public static LocalDateTime toLocalDateTime(final Date date, final ZoneId zoneId) {
        return LocalDateTime.ofInstant(date.toInstant(), zoneId);
    }

    public static boolean isEquals(final Date dateIni, final Date dateEnd) {
        return toLocalDateTime(dateIni).isEqual(toLocalDateTime(dateEnd));
    }

    public static boolean isAfter(final Date dateIni, final Date dateEnd) {
        return toLocalDateTime(dateIni).isAfter(toLocalDateTime(dateEnd));
    }

    public static boolean isBefore(final Date dateIni, final Date dateEnd) {
        return toLocalDateTime(dateIni).isBefore(toLocalDateTime(dateEnd));
    }

    public static boolean lessThanOrEqualTo(final Date dateIni, final Date dateEnd) {
        final LocalDateTime localDateIni = toLocalDateTime(dateIni);
        final LocalDateTime localDateEnd = toLocalDateTime(dateEnd);
        return localDateIni.isEqual(localDateEnd) || localDateIni.isBefore(localDateEnd);
    }

    public static boolean lessThanOrEqualTo(final LocalDateTime dateIni, final LocalDateTime dateEnd) {
        return dateIni.isEqual(dateEnd) || dateIni.isBefore(dateEnd);
    }

    public static boolean greaterthanOrEqualTo(final Date dateIni, final Date dateEnd) {
        final LocalDateTime localDateIni = toLocalDateTime(dateIni);
        final LocalDateTime localDateEnd = toLocalDateTime(dateEnd);
        return localDateIni.isEqual(localDateEnd) || localDateIni.isAfter(localDateEnd);
    }

    public static boolean greaterthanOrEqualTo(final LocalDateTime localDateIni, final LocalDateTime localDateEnd) {
        return localDateIni.isEqual(localDateEnd) || localDateIni.isAfter(localDateEnd);
    }

    public static Date toDate(final LocalDateTime date) {
        return Date.from(
                date.atZone(ZoneId.systemDefault()).toInstant()
        );
    }
}
