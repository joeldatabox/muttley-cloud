package br.com.muttley.model.hermes.notification.onesignal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

import static br.com.muttley.model.hermes.notification.onesignal.MuttleyLanguage.Any;

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

    @JsonCreator
    public Content(final @JsonProperty("language") MuttleyLanguage language, final @JsonProperty("content") String content) {
        this.language = language;
        this.content = content;
    }

    public Content(final String content) {
        this(Any, content);
    }
}
