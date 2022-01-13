package br.com.muttley.report;

import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.model.util.MapUtils;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static br.com.muttley.model.util.MapUtils.getValueByNavigation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;

/**
 * @author Joel Rodrigues Moreira on 27/02/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class JRMuttleyMongoDataSource implements JRDataSource {
    private long currentPageSize = 0;
    protected final MongoTemplate mongoTemplate;
    protected final List<AggregationOperation> operations;

    protected final Class<?> COLLECTION;
    protected final String COLLECTION_NAME;
    protected Iterator<Document> currentResult;
    protected Document currentValue;
    protected long currentSkip = 0l;
    protected final Long currentLimit;
    protected boolean throwsExceptionsIsEmpty = true;

    public JRMuttleyMongoDataSource(final MongoTemplate mongoTemplate, final List<AggregationOperation> operations, Class<?> collection) {
        this(mongoTemplate, operations, 100l, collection);
    }

    public JRMuttleyMongoDataSource(final MongoTemplate mongoTemplate, final List<AggregationOperation> operations, final String collection) {
        this(mongoTemplate, operations, 100l, collection);
    }

    public JRMuttleyMongoDataSource(final MongoTemplate mongoTemplate, final List<AggregationOperation> operations, final long limit, Class<?> collection) {
        this.currentLimit = limit;
        this.mongoTemplate = mongoTemplate;
        this.operations = operations;
        this.COLLECTION = collection;
        this.COLLECTION_NAME = null;
    }

    public JRMuttleyMongoDataSource(final MongoTemplate mongoTemplate, final List<AggregationOperation> operations, final long limit, final String collection) {
        this.currentLimit = limit;
        this.mongoTemplate = mongoTemplate;
        this.operations = operations;
        this.COLLECTION = null;
        this.COLLECTION_NAME = collection;
    }

    @Override
    public boolean next() throws JRException {
        final boolean result;
        if (this.currentResult == null) {
            //se é null quer dizer que estamos na primeira pagina
            result = this.fetchQuery();
            if (this.currentPageSize == 0 && this.throwsExceptionsIsEmpty) {
                throw new MuttleyNoContentException(this.COLLECTION, null, "Nenhum registro encontrado para o relatório!");
            }
        } else {
            result = this.currentResult.hasNext() ? true : this.fetchQuery();
        }
        if (result) {
            this.currentValue = this.currentResult.next();
        }
        return result;
    }

    @Override
    public Object getFieldValue(final JRField jrField) throws JRException {
        //se o campo buscado tiver algum ponto
        //devemos navegar em níveis para recuperar o valor
        /*if (jrField.getName().contains(".")) {
            //pegando a cascata de nivél a ser percorrida
            final String[] fields = jrField.getName().split("\\.");
            //objeto de auxilio para os níveis
            LinkedHashMap<String, Object> linkedHashMap = null;
            //navegando
            for (int i = 0; i < fields.length; i++) {
                //se estivermos no ultimo nívels
                if (i == fields.length - 1) {
                    //basta apenas retornar o valor
                    return linkedHashMap.get(fields[i]);
                } else {
                    //recuperando o nível atual
                    linkedHashMap = (LinkedHashMap<String, Object>) this.currentValue.get(fields[i]);
                }
                //se o nível atual for null apenas retornamos
                if (linkedHashMap == null) {
                    return null;
                }
            }
        }*/
        //retornando o valor de maneira simples
        //return this.currentValue.get(jrField.getName());
        return getValueByNavigation(jrField.getName(), this.currentValue);
    }

    public JRMuttleyMongoDataSource throwsExceptionsIsEmpty(final boolean throwsExceptionsIsEmpty) {
        this.throwsExceptionsIsEmpty = throwsExceptionsIsEmpty;
        return this;
    }

    protected boolean fetchQuery() {
        //Se a ultima consulta  resultou em uma quantidade menor
        //do que o limit máximo trabalhado
        //logo podemos inferir que já percorremos todos os dados
        //do banco
        if (this.currentPageSize > 0 && this.currentPageSize < this.currentLimit) {
            return false;
        }

        final AggregationResults<Document> results;

        if (this.COLLECTION != null) {
            results = this.mongoTemplate.aggregate(
                    this.createAggregationReport(this.operations, this.currentSkip, this.currentLimit),
                    this.COLLECTION,
                    Document.class
            );
        } else {
            results = this.mongoTemplate.aggregate(
                    this.createAggregationReport(this.operations, this.currentSkip, this.currentLimit),
                    this.COLLECTION_NAME,
                    Document.class
            );
        }

        this.currentSkip += this.currentLimit;

        if (results == null || CollectionUtils.isEmpty(results.getMappedResults())) {
            this.currentPageSize = 0;
            return false;
        }

        this.currentPageSize = results.getMappedResults().size();

        this.currentResult = results.getMappedResults().iterator();

        return true;
    }

    protected Aggregation createAggregationReport(final List<AggregationOperation> operations, final long skip, final long limit) {
        //criando uma cópia das operações a serem executadas
        final List<AggregationOperation> newOperations = new ArrayList<>(operations);

        //adicionando skip
        newOperations.add(skip(skip));
        //adicionando limit
        newOperations.add(limit(limit));

        return newAggregation(newOperations);
    }
}
