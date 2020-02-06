package br.com.muttley.mongo.service.codec;

import br.com.muttley.mongo.service.converters.BsonDocumentToZonedDateTimeConverter;
import br.com.muttley.mongo.service.converters.ZonedDateTimeToBsonDocumentConverter;
import org.bson.BasicBSONObject;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.ZonedDateTime;

/**
 * @author Joel Rodrigues Moreira on 22/08/2019.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ZonedDateTimeCodec implements Codec<ZonedDateTime> {

    @Override
    public ZonedDateTime decode(final BsonReader reader, final DecoderContext context) {

        final BasicBSONObject document = new BasicBSONObject();

        reader.readStartDocument();

        document.put("date", new BsonDateTime(reader.readDateTime("date")));
        document.put("offset", new BsonString(reader.readString("offset")));

        reader.readEndDocument();
        return new BsonDocumentToZonedDateTimeConverter().convert(document);
    }

    @Override
    public void encode(final BsonWriter writer, final ZonedDateTime value, final EncoderContext context) {
        final BsonDocument document = new ZonedDateTimeToBsonDocumentConverter().convert(value);

        writer.writeStartDocument();

        writer.writeDateTime("date", document.get("date").asDateTime().getValue());
        writer.writeString("offset", document.get("offset").asString().getValue());

        writer.writeEndDocument();
    }

    @Override
    public Class<ZonedDateTime> getEncoderClass() {
        return ZonedDateTime.class;
    }


}
