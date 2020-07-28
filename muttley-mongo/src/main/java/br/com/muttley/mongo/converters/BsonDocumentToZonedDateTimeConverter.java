package br.com.muttley.mongo.converters;

import org.bson.BasicBSONObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * @author Joel Rodrigues Moreira on 13/05/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
@ReadingConverter
public class BsonDocumentToZonedDateTimeConverter implements Converter<BasicBSONObject, ZonedDateTime> {

    @Override
    public ZonedDateTime convert(final BasicBSONObject source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        return ZonedDateTime.ofInstant(source.getDate("date").toInstant(), ZoneOffset.of(source.getString("offset")));
    }
}
