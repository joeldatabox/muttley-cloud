package br.com.muttley.model.jackson;

import org.springframework.beans.factory.annotation.Value;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Joel Rodrigues Moreira on 22/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Cria um {@link java.text.SimpleDateFormat} que irá serializar por padrão no formato <b>yyyy-MM-dd'T'HH:mm:ss.SSSZ</b>
 */
public class DefaultDateFormatConfig extends SimpleDateFormat {

    public DefaultDateFormatConfig() {
        this("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }

    public DefaultDateFormatConfig(@Value("${br.com.muttley.jackson.date-pattern:yyyy-MM-dd'T'HH:mm:ss.SSSZ}") final String pattern) {
        super(pattern);
        this.setTimeZone(TimeZone.getDefault());
    }

    public DefaultDateFormatConfig(final String pattern, final Locale locale) {
        super(pattern, locale);
    }

    public DefaultDateFormatConfig(final String pattern, final DateFormatSymbols formatSymbols) {
        super(pattern, formatSymbols);
    }

    public static DefaultDateFormatConfig createDateTime() {
        return new DefaultDateFormatConfig();
    }

    public static DefaultDateFormatConfig createDateOnly() {
        return new DefaultDateFormatConfig("yyyy-MM-dd");
    }
}
