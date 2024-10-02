package br.com.muttley.model.security;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
