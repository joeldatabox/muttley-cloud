package br.com.muttley.mongo.service.converters;


import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Created by master on 16/06/17.
 */
@Component
@WritingConverter
public class ZonedDateTimeToBsonDocumentConverter implements Converter<ZonedDateTime, BsonDocument> {

    @Override
    public BsonDocument convert(final ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }
        final BsonDocument document = new BsonDocument("date", new BsonDateTime(
                Date.from(zonedDateTime.toInstant()).getTime()
        ));

        final String offset = zonedDateTime.getOffset().toString();

        document.put("offset", new BsonString(offset));

        final LocalTime hour = LocalTime.parse(offset.replaceAll("[-+]", ""));

        document.put("virtual",
                new BsonDateTime(
                        offset.startsWith("-") ?
                                Date.from(
                                        zonedDateTime
                                                .minusHours(hour.getHour())
                                                .minusMinutes(hour.getMinute())
                                                .toInstant()
                                ).getTime() :
                                Date.from(
                                        zonedDateTime
                                                .plusHours(hour.getHour())
                                                .plusMinutes(hour.getMinute())
                                                .toInstant()
                                ).getTime()
                ));
        return document;
    }
}
