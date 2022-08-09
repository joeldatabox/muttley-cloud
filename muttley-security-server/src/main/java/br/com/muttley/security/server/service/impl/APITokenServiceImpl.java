package br.com.muttley.security.server.service.impl;

import br.com.muttley.model.security.APIToken;
import br.com.muttley.security.server.repository.APITokenRespository;
import br.com.muttley.security.server.service.APITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira on 09/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class APITokenServiceImpl extends SecurityServiceImpl<APIToken> implements APITokenService {

    @Autowired
    public APITokenServiceImpl(APITokenRespository repository, MongoTemplate mongoTemplate) {
        super(repository, mongoTemplate, APIToken.class);
    }
}
