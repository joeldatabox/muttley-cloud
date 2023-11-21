package br.com.muttley.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static br.com.muttley.utils.TimeZoneUtils.getTimezoneFromId;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

/**
 * @author Joel Rodrigues Moreira on 18/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class DateUtils {
    public static final String DATE_REGEX = "(\\d{4}|\\d{5}|\\d{6}|\\d{7})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|[3][01])";
    public static final String DATE_TIME_REGEX = "(\\d{4}|\\d{5}|\\d{6}|\\d{7})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|[3][01])T(00|01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23):(00|01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59):(00|01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59)([.])\\d{3}([+-])\\d{4}";
    public static final DateTimeFormatter DEFAULT_ISO_ZONED_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static final DateTimeFormatter DEFAULT_ISO_LOCAL_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate toLocalDate(final Date date) {
        return toLocalDate(date, ZoneId.systemDefault());
    }

    public static LocalDate toLocalDate(final Date date, final ZoneId zoneId) {
        return date.toInstant()
                .atZone(zoneId)
                .toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(final Date date) {
        return toLocalDateTime(date, ZoneId.systemDefault());
    }

    public static LocalDateTime toLocalDateTime(final LocalDate local) {
        return local.atTime(0, 0);
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
        return toFirstHour(today).isEqual(toFirstHour(dateTime));
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

    public static ZonedDateTime toFirstHour(final ZonedDateTime dateTime) {
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

    public static Date toDate(final LocalDate date) {
        return toDate(date, ZoneId.systemDefault());
    }

    public static Date toDate(final LocalDate date, final ZoneId zoneOffset) {
        return Date.from(date.atStartOfDay(zoneOffset).toInstant());
    }

    public static Date toDate(final LocalDateTime date) {
        return Date.from(
                date.atZone(ZoneId.systemDefault()).toInstant()
        );
    }

    public static Date toDate(final LocalDateTime date, final ZoneOffset zoneOffset) {
        return Date.from(
                date.atZone(zoneOffset).toInstant()
        );
    }

    public static Date toDate(final ZonedDateTime date) {
        return toDate(date, date.getOffset());
    }

    public static Date toDate(final ZonedDateTime date, ZoneOffset zoneOffset) {
        return Date.from(date.toLocalDateTime().toInstant(zoneOffset));
    }

    public static ZonedDateTime toZonedDateTime(final Date date) {
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static ZonedDateTime toZonedDateTime(final Date date, final String offset) {
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.of(offset));
    }

    public static Date toUTC(final Date date) {
        return toDate(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("+0000")));
    }

    public static ZonedDateTime toUTC(final ZonedDateTime zonedDateTime) {
        return ZonedDateTime.ofInstant(zonedDateTime.toInstant(), ZoneId.of("+0000"));
    }

    /**
     * Applica um deslocamento na data e hora levando em consideração o offset
     */
    public static ZonedDateTime applyOffset(final ZonedDateTime zonedDateTime) {
        //recuperando o offset
        final String offset = getTimezoneFromId(zonedDateTime.getOffset().toString());
        //transformando em data
        final LocalTime hour = LocalTime.parse(offset.replaceAll("[-+]", ""));
        //applicando o deslocamento necessário
        return offset.startsWith("-") ?
                zonedDateTime
                        .minusHours(hour.getHour())
                        .minusMinutes(hour.getMinute())
                :
                zonedDateTime
                        .plusHours(hour.getHour())
                        .plusMinutes(hour.getMinute());
    }

    public static Date applyLocalSystemOffset(final Date date) {
        return applyOffset(date, getTimezoneFromId(ZoneId.systemDefault().toString()));
    }

    public static Date applyOffset(final Date date, final ZoneId zoneId) {
        return applyOffset(date, getTimezoneFromId(zoneId.toString()));
    }

    public static Date applyOffset(final Date date, final ZoneOffset offsetTime) {
        return applyOffset(date, getTimezoneFromId(offsetTime.toString()));
    }

    public static Date applyOffset(final Date date, final String offset) {
        //transformando em data
        final LocalTime hour = LocalTime.parse(offset.replaceAll("[-+]", ""));
        //applicando o deslocamento necessário
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.of(offset));

        return Date.from(offset.startsWith("-") ?
                zonedDateTime
                        .minusHours(hour.getHour())
                        .minusMinutes(hour.getMinute())
                        .toInstant()
                :
                zonedDateTime
                        .plusHours(hour.getHour())
                        .plusMinutes(hour.getMinute())
                        .toInstant()
        );
    }

    public static boolean isValidDate(final String value) {
        return String.valueOf((Object) value).matches(DATE_REGEX);
    }

    public static boolean isValidDateTime(final String value) {
        return String.valueOf((Object) value).matches(DATE_TIME_REGEX);
    }
}
