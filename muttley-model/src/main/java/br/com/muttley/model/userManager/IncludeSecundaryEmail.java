package br.com.muttley.model.userManager;

import br.com.muttley.model.security.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncludeSecundaryEmail {


    private String emailSecundary;

}
