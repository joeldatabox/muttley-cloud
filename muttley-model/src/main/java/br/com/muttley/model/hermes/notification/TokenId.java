package br.com.muttley.model.hermes.notification;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

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
