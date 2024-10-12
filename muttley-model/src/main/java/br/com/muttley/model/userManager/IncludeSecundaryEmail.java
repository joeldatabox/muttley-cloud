package br.com.muttley.model.userManager;

import br.com.muttley.model.security.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncludeSecundaryEmail {


    @Email(message = "Informe um email secundário válido!")
    private String emailSecundary;

}
