package br.com.muttley.model.security.merge;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 10/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
public class MergedUserBaseResponse {
    private List<MergedUserBaseItemResponse> itens;


    public MergedUserBaseResponse() {
    }

    @JsonCreator
    public MergedUserBaseResponse(@JsonProperty("itens") final List<MergedUserBaseItemResponse> itens) {
        this.itens = itens;
    }

    public MergedUserBaseResponse add(final MergedUserBaseItemResponse item) {
        this.prepareItens();
        this.itens.add(item);
        return this;
    }

    private MergedUserBaseResponse add(final String email, final Status status) {
        return this.add(new MergedUserBaseItemResponse(email, status));
    }

    private void prepareItens() {
        if (itens == null) {
            this.itens = new ArrayList<>();
        }
    }
}
