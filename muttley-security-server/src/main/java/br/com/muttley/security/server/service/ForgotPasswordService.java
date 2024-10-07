package br.com.muttley.security.server.service;


import br.com.muttley.model.recoveryPassword.ResetPasswordRequest;

/**
 * @author Carolina Cedro on 06/10/2024.
 * e-mail: <a href="mailto:ana.carolina@maxxsoft.com">ana.carolina@maxxsoft.com</a>
 * @project muttley-cloud
 */
public interface ForgotPasswordService<T> {

    T forgotPassword(final String email);

    void resetPassword(final ResetPasswordRequest request);
}
