/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.muttley.mongo.query.model2;


import br.com.muttley.model.util.BigDecimalUtil;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.math.BigDecimal;

import static br.com.muttley.model.util.BigDecimalUtil.setDefaultScale;

/**
 * @author Joel Rodrigues Moreira on 21/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

public class NotaFiscalItem {

    @DBRef
    private Produto produto;

    @DBRef
    private Cultura cultura;
    private BigDecimal vlrLiquido;
    private Float qtde;

    public NotaFiscalItem() {
        this.vlrLiquido = BigDecimalUtil.newZero();
    }

    public BigDecimal getVlrLiquido() {
        return vlrLiquido;
    }

    public void setVlrLiquido(BigDecimal vlrLiquido) {
        this.vlrLiquido = setDefaultScale(vlrLiquido);
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Cultura getCultura() {
        return cultura;
    }

    public NotaFiscalItem setCultura(final Cultura cultura) {
        this.cultura = cultura;
        return this;
    }

    public Float getQtde() {
        return qtde;
    }

    public NotaFiscalItem setQtde(final Float qtde) {
        this.qtde = qtde;
        return this;
    }
}
