package br.com.muttley.security.server.controller.auth;

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

//    protected final JwtTokenUtilService jwtTokenUtil;
//    protected final UserService userService;
//    protected final PasswordService passwordService;

    @Autowired
    public ForgotPasswordController(final ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }


    @RequestMapping(value = "/forgot-password", method = POST)
    public ResponseEntity forgotPassword(@RequestBody UserForgotPasswordRequest request) {
        return ResponseEntity.ok(this.forgotPasswordService.forgotPassword(request.getEmail()));

    }

}
