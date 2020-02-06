package br.com.muttley.mongo.service.converters;

/**
 * Created by master on 16/06/17.
 */

import org.bson.BasicBSONObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

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
