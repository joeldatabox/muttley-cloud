package br.com.muttley.security.server.service.impl;

import br.com.muttley.model.recoveryPassword.ResetPasswordRequest;
import br.com.muttley.model.security.User;
import br.com.muttley.security.server.repository.UserRepository;
import br.com.muttley.security.server.service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final JavaMailSender mailSender;

    private final UserRepository userRepository;

    @Autowired
    public ForgotPasswordServiceImpl(final JavaMailSender mailSender, final UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }


    @Override
    public ResponseEntity forgotPassword(String email) {


        User user = userRepository.findByEmail(email);

        if (user != null) {
            // Geração de token de recuperação de senha
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            userRepository.save(user);

            // Enviando o email de recuperação
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Recuperação de Senha");
            mailMessage.setText("Para redefinir sua senha, clique no link abaixo:\n" +
                    "http://localhost:4200/reset-password?token=" + token);

            mailSender.send(mailMessage);
        }

        return null;

    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

    }
}
