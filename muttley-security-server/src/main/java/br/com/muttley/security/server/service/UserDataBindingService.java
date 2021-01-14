package br.com.muttley.security.server.service;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserDataBinding;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira 12/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface UserDataBindingService {

    UserDataBinding save(final User user, final UserDataBinding dataBinding);

    UserDataBinding update(final User user, final UserDataBinding dataBinding);

    /**
     * Lista os itens levando em consideração não o usuário da requisição,
     * mas sim o userName informado
     *
     * @param user     -> usuário da requisição corrente
     * @param userName -> nome de usuário desejado
     */
    @PreAuthorize(
            "this.isCheckRole()? " +
                    "( " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString() " +
                    "   ) " +
                    "or " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_USER_DATA_BINDING_OTHERS_USERS_MERGE " +
                    "   ) " +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).ROLE_MOBILE_USER_DATA_BINDING_OTHERS_USERS_MERGE" +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    List<UserDataBinding> listByUserName(final User user, final String userName);

    @PreAuthorize(
            "this.isCheckRole()? " +
                    "( " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString() " +
                    "   ) " +
                    "or " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_USER_DATA_BINDING_OTHERS_USERS_MERGE " +
                    "   ) " +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).ROLE_MOBILE_USER_DATA_BINDING_OTHERS_USERS_MERGE" +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    UserDataBinding saveByUserName(final User user, final String userName, final UserDataBinding value);

    @PreAuthorize(
            "this.isCheckRole()? " +
                    "( " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString() " +
                    "   ) " +
                    "or " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_USER_DATA_BINDING_OTHERS_USERS_MERGE " +
                    "   ) " +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).ROLE_MOBILE_USER_DATA_BINDING_OTHERS_USERS_MERGE" +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    UserDataBinding updateByUserName(final User user, final String userName, final UserDataBinding dataBinding);

    void merge(final User user, final UserDataBinding dataBinding);
}
