package br.com.muttley.security.infra.service.impl;

import br.com.muttley.model.security.jwt.JwtUser;
import br.com.muttley.model.security.model.User;
import br.com.muttley.security.infra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Serviço necessario por recuperar usuarios vindos do banco de dados
 *
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Autowired
    public UserDetailServiceImpl(final UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        } else {
            return new JwtUser(user);
        }
    }
}
