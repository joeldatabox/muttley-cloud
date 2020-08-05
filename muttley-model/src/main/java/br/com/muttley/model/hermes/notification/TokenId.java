package br.com.muttley.model.hermes.notification;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Joel Rodrigues Moreira on 03/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = {"token", "origin"})
public class TokenId {
    @NotBlank(message = "Informe um token válido")
    private String token;
    private boolean mobile = true;
    @NotNull(message = "Informe o origin")
    private TokenOrigin origin;
}

