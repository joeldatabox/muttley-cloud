package br.com.muttley.model.hermes.notification.onesignal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "language")
public class Content {
    @NotNull
    private MuttleyLanguage language;
    @NotBlank
    private String content;

    public Content() {
    }

    public Content(MuttleyLanguage language, String content) {
        this.language = language;
        this.content = content;
    }
}
