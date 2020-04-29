package br.com.muttley.model.notification;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = {"token", "origin"})
public class TokenId {
    private String token;
    private TokenId origin;
}
