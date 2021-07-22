package br.com.muttley.feign.service.registrar;

import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Joel Rodrigues Moreira on 21/07/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class MuttleyFeignFormatterRegister implements FeignFormatterRegistrar {
    @Override
    public void registerFormatters(final FormatterRegistry registry) {
        registry.addFormatter(new DateFormatter());
    }

    private static class DateFormatter implements Formatter<Date> {
        static final ThreadLocal<SimpleDateFormat> FORMAT = ThreadLocal.withInitial(
                () -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        );

        @Override
        public Date parse(String text, Locale locale) throws ParseException {
            return FORMAT.get().parse(text);
        }

        @Override
        public String print(Date date, Locale locale) {
            return FORMAT.get().format(date);
        }
    }
}
