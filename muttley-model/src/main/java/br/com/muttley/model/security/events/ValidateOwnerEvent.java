package br.com.muttley.model.security.events;

import br.com.muttley.model.Model;
import br.com.muttley.model.security.User;

/**
 * Evento disparado toda vez que se tenta inserir um novo registro do tipo {@link Model}
 * O mesmo se faz necessário para não haver vazamento de segurança.
 * <p>
 * Por padrão só iremos aceitar owner caso arequisição venha do servidor odin,
 * caso contrario pegaremos o owner da requisição corrente
 *
 * @author Joel Rodrigues Moreira on 08/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ValidateOwnerEvent extends AbstractValidateOwnerEvent<Model> {
    public ValidateOwnerEvent(User currenteUserFromRequest, Model source) {
        super(currenteUserFromRequest, source);
    }
}
