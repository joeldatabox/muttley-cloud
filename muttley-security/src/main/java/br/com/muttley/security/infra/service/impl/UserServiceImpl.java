package br.com.muttley.security.infra.service.impl;

import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.exception.throwables.security.MuttleySecurityConflictException;
import br.com.muttley.exception.throwables.security.MuttleySecurityNotFoundException;
import br.com.muttley.model.security.jwt.JwtUser;
import br.com.muttley.model.security.model.Passwd;
import br.com.muttley.model.security.model.User;
import br.com.muttley.security.infra.repository.UserRepository;
import br.com.muttley.security.infra.response.JwtTokenResponse;
import br.com.muttley.security.infra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final String tokenHeader;

    @Autowired
    public UserServiceImpl(final UserRepository repository, @Value("${muttley.security.jwt.controller.tokenHeader:Authorization}") final String tokenHeader) {
        this.repository = repository;
        this.tokenHeader = tokenHeader;
    }

    @Override
    public User save(final User user) {
        return merge(user);
    }

    @Override
    public boolean remove(final User user) {
        final User other = findById(user.getId());
        repository.delete(other);
        return true;
    }

    @Override
    public User update(final User user) {
        return merge(user);
    }

    @Override
    public User updatePasswd(final Passwd passwd) {
        final User user = findById(passwd.getId());
        user.setPasswd(passwd);
        return save(user);
    }

    @Override
    public User findByEmail(final String email) {
        final User user = repository.findByEmail(email);
        if (user == null) {
            throw new MuttleySecurityNotFoundException(User.class, "email", email + " este registro não foi encontrado");
        }
        return user;
    }

    @Override
    public User findById(final String id) {
        final User user = repository.findOne(id);
        if (user == null) {
            throw new MuttleySecurityNotFoundException(User.class, "id", id + " este registro não foi encontrado");
        }
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return (Collection<User>) repository.findAll();
    }

    @Override
    public Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public JwtUser getCurrentJwtUser() {
        return (JwtUser) getCurrentAuthentication().getPrincipal();
    }

    @Override
    public JwtTokenResponse getCurrentToken() {
        return new JwtTokenResponse(
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest()
                        .getHeader(this.tokenHeader)
        );
    }

    @Override
    public User getCurrentUser() {
        return getCurrentJwtUser().getOriginUser();
    }

    private User merge(final User user) {
        if (user.getNome() == null || user.getNome().length() < 4) {
            throw new MuttleySecurityBadRequestException(User.class, "nome", "O campo nome deve ter de 4 a 200 caracteres!");
        }
        if (!user.isValidEmail()) {
            throw new MuttleySecurityBadRequestException(User.class, "email", "Informe um email válido!");
        }
        if (user.getId() == null) {
            //validando se já existe esse usuário no sistema
            try {
                if (findByEmail(user.getEmail()) != null) {
                    throw new MuttleySecurityConflictException(User.class, "email", "Email já cadastrado!");
                }
            } catch (MuttleySecurityNotFoundException ex) {
            }
            //validando se preencheu a senha corretamente
            if (!user.isValidPasswd()) {
                throw new MuttleySecurityBadRequestException(User.class, "passwd", "Informe uma senha válida!");
            }
            return repository.save(user);
        } else {
            final User self = findById(user.getId());

            if (!self.getEmail().equals(user.getEmail())) {
                throw new MuttleySecurityBadRequestException(User.class, "email", "O email não pode ser modificado!").setStatus(HttpStatus.NOT_ACCEPTABLE);
            }

            //garantindo que a senha não irá ser modificada
            user.setPasswd(self);

            return repository.save(user);
        }
    }


    public Long count(final User user, final Map<String, Object> allRequestParams) {
        //return repository.count(allRequestParams);
        throw new UnsupportedOperationException("Implemente o methodo");
    }


    public List<User> findAll(final User user, final Map<String, Object> allRequestParams) {
        throw new UnsupportedOperationException("Implemente o methodo");
        //return repository.findAll(allRequestParams);
    }
}
