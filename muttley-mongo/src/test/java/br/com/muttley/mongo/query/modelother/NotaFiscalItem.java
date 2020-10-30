package br.com.muttley.mongo.query.modelother;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.math.BigDecimal;

/**
 * @author Joel Rodrigues Moreira on 20/10/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class NotaFiscalItem {

    @DBRef
    private Produto produto;
    @DBRef
    private BigDecimal vlrLiquido;
    private Float qtde;

}
