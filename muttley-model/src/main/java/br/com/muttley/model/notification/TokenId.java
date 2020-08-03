package br.com.muttley.model.notification;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
    private String token;
    private TokenId origin;
}

