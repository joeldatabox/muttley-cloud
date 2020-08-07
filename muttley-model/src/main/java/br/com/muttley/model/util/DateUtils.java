package br.com.muttley.model.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

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

    public static boolean isToday(final Date date) {
        return isEquals(toFirstHour(date), toFirstHour(new Date()));
    }

    public static boolean isToday(final LocalDateTime dateTime) {
        return toFirstHour(dateTime).equals(toFirstHour(LocalDateTime.now()));
    }

    public static boolean isToday(final ZonedDateTime dateTime) {
        final ZonedDateTime today;
        if (dateTime.getZone() == null) {
            today = ZonedDateTime.now();
        } else {
            today = ZonedDateTime.now(dateTime.getZone());
        }
        return tofirstHour(today).isEqual(tofirstHour(dateTime));
    }

    public static Date toFirstHour(final Date date) {
        return toDate(toFirstHour(toLocalDateTime(date)));
    }

    public static LocalDateTime toFirstHour(final LocalDateTime date) {
        return date
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    public static ZonedDateTime tofirstHour(final ZonedDateTime dateTime) {
        return dateTime
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    public static Date toLastHour(final Date date) {
        return toDate(toLastHour(toLocalDateTime(date)));
    }

    public static LocalDateTime toLastHour(final LocalDateTime date) {
        return date
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999999999);
    }

    public static ZonedDateTime toLastHour(final ZonedDateTime dateTime) {
        return dateTime.withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999999999);
    }

    public static Date toFirstDayOfMonth(final Date date) {
        return toDate(toFirstDayOfMonth(toLocalDateTime(date)));
    }

    public static LocalDateTime toFirstDayOfMonth(final LocalDateTime date) {
        return date.with(firstDayOfMonth());
    }

    public static Date toLastDayOfMonth(final Date date) {
        return toDate(toLastDayOfMonth(toLocalDateTime(date)));
    }

    public static LocalDateTime toLastDayOfMonth(final LocalDateTime date) {
        return date.with(lastDayOfMonth());
    }

    public static Date toDate(final LocalDateTime date) {
        return Date.from(
                date.atZone(ZoneId.systemDefault()).toInstant()
        );
    }

    public static Date toDate(final ZonedDateTime date) {
        return toDate(date, date.getOffset());
    }

    public static Date toDate(final ZonedDateTime date, ZoneOffset zoneOffset) {
        return Date.from(date.toLocalDateTime().toInstant(zoneOffset));
    }
}
