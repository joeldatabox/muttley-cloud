package br.com.muttley.security.server.controller.auth;

import br.com.muttley.exception.throwables.security.MuttleySecurityUserNotFoundException;
import br.com.muttley.model.recoveryPassword.ResetPasswordRequest;
import br.com.muttley.model.recoveryPassword.UserForgotPasswordRequest;
import br.com.muttley.security.server.service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/api/v1/recovery", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class ForgotPasswordController {


    protected final ForgotPasswordService forgotPasswordService;

    @Autowired
    public ForgotPasswordController(final ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }


    @RequestMapping(value = "/forgot-password", method = POST)
    public ResponseEntity forgotPassword(@RequestBody UserForgotPasswordRequest request) {

        if (request.getEmail().isEmpty()) {
            throw new MuttleySecurityUserNotFoundException(UserForgotPasswordRequest.class, "email", "O campo de email não pode estar vazio. Por favor, preencha e tente novamente.");
        }

        return ResponseEntity.ok(this.forgotPasswordService.forgotPassword(request.getEmail()));

    }

    @RequestMapping(value = "/reset-password", method = POST)
    public ResponseEntity resetPassword(@RequestBody ResetPasswordRequest request) {
        if (request.getNewPassword().isEmpty()) {
            throw new MuttleySecurityUserNotFoundException(ResetPasswordRequest.class, "novaSenha", "O campo de nova senha não pode estar vazio. Por favor, preencha e tente novamente.");
        }

        if (request.getToken().isEmpty()) {
            throw new MuttleySecurityUserNotFoundException(ResetPasswordRequest.class, "token", "O campo de token não pode estar vazio. Por favor, preencha e tente novamente.");
        }
        return ResponseEntity.ok(this.forgotPasswordService.resetPassword(request));
    }


}
