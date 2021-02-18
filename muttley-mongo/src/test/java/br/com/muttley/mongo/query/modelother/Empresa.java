package br.com.muttley.mongo.query.modelother;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 20/10/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class Empresa {
    @Transient
    @JsonIgnore
    public static final String COLLECTION_EMPRESAS = "empresas";
    @Transient
    @JsonIgnore
    public static final String COLLECTION_FILIAIS = "filiais";

    @Id
    protected String id;

    protected String razao;


    protected String fantasia;


    protected String cnpj;

    protected String inscEstadual;
    protected String telefone;


    protected String email;

    protected String site;

    protected String sync;

    protected Date dtSync;
}
