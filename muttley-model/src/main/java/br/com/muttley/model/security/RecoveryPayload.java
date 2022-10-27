package br.com.muttley.model.security;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Joel Rodrigues Moreira on 27/10/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class RecoveryPayload {
    private String email;
    private boolean renewCode;
    private String codeVerification;
    private String seedVerification;
}
