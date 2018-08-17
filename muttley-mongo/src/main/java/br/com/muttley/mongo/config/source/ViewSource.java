package br.com.muttley.mongo.config.source;

import org.bson.BsonBoolean;
import org.bson.BsonValue;
import org.bson.conversions.Bson;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 18/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface ViewSource {
    static final BsonValue _TRUE = new BsonBoolean(true);

    /**
     * Deve retornar a versão da view
     */
    String getVersion();

    /**
     * Deve retornar o nome da view
     */
    String getViewName();

    /**
     * Deve retornar o nome da collection sobre a qual a view irá funcionar
     */
    String getViewOn();

    /**
     * Cadeia de pipelines para gerar a view
     */
    List<? extends Bson> getPipeline();

    /**
     * Deve retornar uma breve descrição da view
     */
    String getDescription();
}
