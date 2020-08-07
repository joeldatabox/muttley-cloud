package br.com.muttley.model.hermes.notification.onesignal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Joel Rodrigues Moreira on 02/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
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
