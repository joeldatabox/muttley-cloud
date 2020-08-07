package br.com.muttley.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Joel Rodrigues Moreira on 13/05/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class VersionDocument {
    /**
     * Nome do cliente que criou o documento
     */
    private String originNameClientCreate;

    /**
     * Versão do cliente que criou o documento
     */
    private String originVersionClientCreate;

    /**
     * Versão do servidor em que foi criado o documento
     */
    private String serverVersionCreate;

    /**
     * Nome do cliente que fez a atualização do documento
     */
    private String originNameClientLastUpdate;

    /**
     * Versão do cliente que fez a ultima atualização do documento
     */
    private String originVersionClientLastUpdate;

    /**
     * Versão do servidor em que foi atualizado o documento
     */
    private String serverVersionLastUpdate;

    public VersionDocument() {
    }

    @JsonCreator
    public VersionDocument(
            @JsonProperty("originNameClientCreate") final String originNameClientCreate,
            @JsonProperty("originVersionClientCreate") final String originVersionClientCreate,
            @JsonProperty("serverVersionCreate") final String serverVersionCreate,
            @JsonProperty("originNameClientLastUpdate") final String originNameClientLastUpdate,
            @JsonProperty("originVersionClientLastUpdate") final String originVersionClientLastUpdate,
            @JsonProperty("serverVersionLastUpdate") final String serverVersionLastUpdate) {
        this.originNameClientCreate = originNameClientCreate;
        this.originVersionClientCreate = originVersionClientCreate;
        this.serverVersionCreate = serverVersionCreate;
        this.originNameClientLastUpdate = originNameClientLastUpdate;
        this.originVersionClientLastUpdate = originVersionClientLastUpdate;
        this.serverVersionLastUpdate = serverVersionLastUpdate;
    }
}
