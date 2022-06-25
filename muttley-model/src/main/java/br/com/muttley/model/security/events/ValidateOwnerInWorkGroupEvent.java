package br.com.muttley.model.security.events;

import br.com.muttley.model.security.Passaport;
import br.com.muttley.model.security.User;

/**
 * Evento disparado toda vez que se tenta inserir um novo grupo de trabalho
 * O mesmo se faz necessário para não haver vazamento de segurança.
 * <p>
 * Por padrão só iremos aceitar owner caso arequisição venha do servidor odin,
 * caso contrario pegaremos o owner da requisição corrente
 *
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ValidateOwnerInWorkGroupEvent extends AbstractValidateOwnerEvent<Passaport> {
    public ValidateOwnerInWorkGroupEvent(final User currenteUserFromRequest, final Passaport passaport) {
        super(currenteUserFromRequest, passaport);
    }
}
