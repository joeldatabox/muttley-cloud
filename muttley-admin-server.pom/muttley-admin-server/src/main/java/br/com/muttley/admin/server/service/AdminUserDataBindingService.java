package br.com.muttley.admin.server.service;

import br.com.muttley.model.admin.AdminUserDataBinding;
import br.com.muttley.model.security.KeyUserDataBinding;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserData;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira 12/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface AdminUserDataBindingService {

    @PreAuthorize(
            "this.isCheckRole()? " +
                    "( " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString() " +
                    "   ) " +
                    "or " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_USER_DATA_BINDING_CREATE.toString() " +
                    "   ) " +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).ROLE_MOBILE_USER_DATA_BINDING_CREATE.toString()  " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    AdminUserDataBinding save(final User user, final AdminUserDataBinding dataBinding);

    @PreAuthorize(
            "this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString() " +
                    "   ) " +
                    "or " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_USER_DATA_BINDING_UPDATE.toString() " +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).ROLE_MOBILE_USER_DATA_BINDING_UPDATE.toString() " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    AdminUserDataBinding update(final User user, final AdminUserDataBinding dataBinding);

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
                    "       T(br.com.muttley.model.security.Role).ROLE_USER_DATA_BINDING_OTHERS_USERS_MERGE.toString() " +
                    "   ) " +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).ROLE_MOBILE_USER_DATA_BINDING_OTHERS_USERS_MERGE.toString()" +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    List<AdminUserDataBinding> listByUserName(final User user, final String userName);

    @PreAuthorize(
            "this.isCheckRole()? " +
                    "( " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString() " +
                    "   ) " +
                    "or " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_USER_DATA_BINDING_READ.toString() " +
                    "   ) " +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).ROLE_MOBILE_USER_DATA_BINDING_READ.toString()  " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    List<AdminUserDataBinding> listBy(final User user);

    @PreAuthorize(
            "this.isCheckRole()? " +
                    "( " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString() " +
                    "   ) " +
                    "or " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_USER_DATA_BINDING_OTHERS_USERS_MERGE.toString() " +
                    "   ) " +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).ROLE_MOBILE_USER_DATA_BINDING_OTHERS_USERS_MERGE.toString()" +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    AdminUserDataBinding saveByUserName(final User user, final String userName, final AdminUserDataBinding value);

    @PreAuthorize(
            "this.isCheckRole()? " +
                    "( " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString() " +
                    "   ) " +
                    "or " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_USER_DATA_BINDING_OTHERS_USERS_MERGE.toString() " +
                    "   ) " +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).ROLE_MOBILE_USER_DATA_BINDING_OTHERS_USERS_MERGE.toString()" +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    AdminUserDataBinding updateByUserName(final User user, final String userName, final AdminUserDataBinding dataBinding);

    @PreAuthorize(
            "this.isCheckRole()? " +
                    "( " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString() " +
                    "   ) " +
                    "or " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_USER_DATA_BINDING_OTHERS_USERS_MERGE.toString() " +
                    "   ) " +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).ROLE_MOBILE_USER_DATA_BINDING_OTHERS_USERS_MERGE.toString()" +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    void merge(final User user, final String userName, final AdminUserDataBinding dataBinding);

    @PreAuthorize(
            "this.isCheckRole()? " +
                    "( " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString() " +
                    "   ) " +
                    "or " +
                    "   hasAnyRole( " +
                    "       T(br.com.muttley.model.security.Role).ROLE_USER_DATA_BINDING_OTHERS_USERS_MERGE.toString() " +
                    "   ) " +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).ROLE_MOBILE_USER_DATA_BINDING_OTHERS_USERS_MERGE.toString()" +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true "
    )
    void merge(final User user, final String userName, final Set<AdminUserDataBinding> dataBindings);

    AdminUserDataBinding getKey(final User user, final KeyUserDataBinding key);

    AdminUserDataBinding getKey(final User user, final String key);

    AdminUserDataBinding getKeyByUserName(final User user, final String userName, final KeyUserDataBinding key);

    AdminUserDataBinding getKeyByUserName(final User user, final String userName, final String key);

    boolean contains(final User user, final KeyUserDataBinding key);

    boolean contains(final User user, final String key);

    boolean containsByUserNameAndKey(final User user, final String userName, final KeyUserDataBinding key);

    boolean containsByUserNameAndKey(final User user, final String userName, final String key);

    /**
     * Verifica se uma determina chave e valor já esta reservado para algum usuário
     */
    boolean containsByKeyAndValue(final User user, final KeyUserDataBinding key, final String value);

    /**
     * Verifica se uma determina chave e valor já esta reservado para algum usuário
     */
    boolean containsByKeyAndValue(final User user, final String key, final String value);

    /**
     * Verifica se uma determina chave e valor já esta reservado para algum usuário diferente do username informado
     */
    boolean containsByKeyAndValueAndUserNameNotEq(final User user, final String userName, final KeyUserDataBinding key, final String value);

    /**
     * Verifica se uma determina chave e valor já esta reservado para algum usuário diferente do username informado
     */
    boolean containsByKeyAndValueAndUserNameNotEq(final User user, final String userName, final String key, final String value);

    UserData getUserBy(final User user, final KeyUserDataBinding key, final String value);

    UserData getUserBy(final User user, final String key, final String value);
}
