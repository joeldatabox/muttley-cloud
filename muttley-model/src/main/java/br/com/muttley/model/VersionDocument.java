package br.com.muttley.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Joel Rodrigues Moreira on 30/01/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class VersionDocument {
    /**
     * Nome do cliente que criou o documento
     */
    @Getter
    @Setter
    private String originNameClientCreate;
    /**
     * Versão do cliente que criou o documento
     */
    @Getter
    @Setter
    private String originVersionClientCreate;
    /**
     * Versão do servidor em que foi criado o documento
     */
    @Getter
    @Setter
    private String serverVersionCreate;
    /**
     * Nome do cliente que fez a atualização do documento
     */
    @Getter
    @Setter
    private String clientOriginNameLastUpdate;
    /**
     * Versão do cliente que fez a ultima atualização do documento
     */
    @Getter
    @Setter
    private String originVersionClientLastUpdate;
    /**
     * Versão do servidor em que foi atualizado o documento
     */
    @Getter
    @Setter
    private String serverVersionLastUpdate;

    public VersionDocument() {
    }

    @JsonCreator
    public VersionDocument(
            @JsonProperty("originNameClientCreate") final String originNameClientCreate,
            @JsonProperty("originVersionClientCreate") final String originVersionClientCreate,
            @JsonProperty("serverVersionCreate") final String serverVersionCreate,
            @JsonProperty("clientOriginNameLastUpdate") final String clientOriginNameLastUpdate,
            @JsonProperty("originVersionClientLastUpdate") final String originVersionClientLastUpdate,
            @JsonProperty("serverVersionLastUpdate") final String serverVersionLastUpdate) {
        this.originNameClientCreate = originNameClientCreate;
        this.originVersionClientCreate = originVersionClientCreate;
        this.serverVersionCreate = serverVersionCreate;
        this.clientOriginNameLastUpdate = clientOriginNameLastUpdate;
        this.originVersionClientLastUpdate = originVersionClientLastUpdate;
        this.serverVersionLastUpdate = serverVersionLastUpdate;
    }
}
