package br.com.muttley.mongo.converters;

import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonWriter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

import static br.com.muttley.model.TimeZoneDocument.getTimezoneFromId;
import static br.com.muttley.model.util.DateUtils.toDate;

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
        final BsonDocument document = new BsonDocument("date", new BsonDateTime(Date.from(zonedDateTime.toInstant()).getTime()));
        document.put("offset", new BsonString(getTimezoneFromId(zonedDateTime.getOffset().toString())));
        return document;
    }

    public void convert(final BsonWriter writer, final ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            writer.writeNull();
        } else {
            writer.writeStartDocument();
            writer.writeString("offset", getTimezoneFromId(zonedDateTime.getOffset().toString()));
            writer.writeDateTime("date", toDate(zonedDateTime).getTime());
            writer.writeEndDocument();
        }
    }
}
