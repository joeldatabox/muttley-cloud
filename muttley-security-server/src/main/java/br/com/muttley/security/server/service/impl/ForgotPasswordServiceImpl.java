package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.security.MuttleySecurityEmailNotFoundtException;
import br.com.muttley.exception.throwables.security.MuttleySecurityExpiredTokenException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUserNotFoundException;
import br.com.muttley.model.recoveryPassword.ResetPasswordRequest;
import br.com.muttley.model.security.User;
import br.com.muttley.security.server.repository.UserRepository;
import br.com.muttley.security.server.service.ForgotPasswordService;
import br.com.muttley.security.server.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final JavaMailSender mailSender;
    private final PasswordService passwordService;


    private final UserRepository userRepository;


    @Autowired
    public ForgotPasswordServiceImpl(final JavaMailSender mailSender, PasswordService passwordService, final UserRepository userRepository) {
        this.mailSender = mailSender;
        this.passwordService = passwordService;
        this.userRepository = userRepository;
    }


    @Override
    public ResponseEntity<?> forgotPassword(String email) {
        try {


            User user = userRepository.findByEmail(email);
            User userByEmailSecundary = userRepository.findByEmailSecundario(email);

            if (user != null || userByEmailSecundary != null) {
                // Gera um token de redefinição de senha e associa ao usuário
                String token = UUID.randomUUID().toString();
                User foundUser = user != null ? user : userByEmailSecundary;
                foundUser.setResetToken(token);
                userRepository.save(foundUser);

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom("noreply@maxxsoft.com.br");
                helper.setSubject("Redefinição de Senha");
                helper.addTo(email);

                String emailTemplate = pathForTemplateEmail("recovery-password-template.html");
                emailTemplate = emailTemplate.replace("#{token}", token);

                helper.setText(emailTemplate, true);
                mailSender.send(message);

                return ResponseEntity.ok("Email de recuperação de senha enviado com sucesso.");
            } else {
                throw new MuttleySecurityEmailNotFoundtException(User.class, "email", "Email não encontrado. Contate o suporte.");
            }

        } catch (MessagingException | IOException e) {
            throw new RuntimeException("Falha ao enviar o email de recuperação de senha. Tente novamente mais tarde.", e);
        }
    }


    @Override
    public ResponseEntity<?> resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByResetToken(request.getToken());

        if (user == null) {
            throw new MuttleySecurityUserNotFoundException(User.class, "token", "Token expirado. Solicite uma nova recuperação de senha.");
        }


        // Verifica se o token está expirado
        if (user.getResetTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new MuttleySecurityExpiredTokenException(User.class, "token", "Token expirado. Solicite uma nova recuperação de senha.");
        }

        passwordService.resetePasswordFor(user, request.getNewPassword());
        user.setResetToken(null);
        user.setResetTokenExpiryDate(null);
        userRepository.save(user);

        return ResponseEntity.ok("Senha redefinida com sucesso.");
    }


    public String pathForTemplateEmail(String template) throws IOException {
        ClassPathResource resource = new ClassPathResource(template);

        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }
        return contentBuilder.toString();
    }


}
