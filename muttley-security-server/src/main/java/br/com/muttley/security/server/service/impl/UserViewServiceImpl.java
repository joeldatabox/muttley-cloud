package br.com.muttley.security.server.service.impl;

import br.com.muttley.domain.impl.ServiceImpl;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserView;
import br.com.muttley.security.server.service.UserViewService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Joel Rodrigues Moreira on 15/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class UserViewServiceImpl extends ServiceImpl<UserView> implements UserViewService {
    private final MongoTemplate template;
    private static final String VIEW = "view_muttley_users";

    @Autowired
    public UserViewServiceImpl(final MongoTemplate mongoTemplate) {
        super(null, UserView.class);
        this.template = mongoTemplate;
    }

    @Override
    public void checkPrecondictionSave(final User user, final UserView value) {
        throw new MuttleyBadRequestException(UserView.class, null, "Não se pode inserir registro em uma view");
    }

    @Override
    public void checkPrecondictionUpdate(final User user, final UserView value) {
        throw new MuttleyBadRequestException(UserView.class, null, "Não se pode alterar registro em uma view");
    }

    @Override
    public void checkPrecondictionDelete(final User user, final String id) {
        throw new MuttleyBadRequestException(UserView.class, null, "Não se pode deletar registro em uma view");
    }

    @Override
    public UserView findById(final User user, final String id) {
        final UserView view = this.template.findOne(query(where("_id").is(new ObjectId(id))), UserView.class, VIEW);
        if (view == null) {
            throw new MuttleyNotFoundException(UserView.class, "id", "Registro não encontrado");
        }
        return view;
    }

    @Override
    public UserView findFirst(final User user) {
        return super.findFirst(user);
    }
}
