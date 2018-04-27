package br.com.muttley.model.jackson;

import br.com.muttley.model.jackson.converter.ObjectIdSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

/**
 * @author Joel Rodrigues Moreira on 27/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class JsonHelper {

    public static String toJson(final Object obj) {
        if (obj == null) {
            return "{}";
        }

        try {
            return new ObjectMapper()
                    .setVisibility(FIELD, ANY)
                    .registerModule(
                            new SimpleModule("ObjectId",
                                    new Version(1, 0, 0, null, null, null)
                            ).addSerializer(ObjectId.class, new ObjectIdSerializer())
                    ).writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
