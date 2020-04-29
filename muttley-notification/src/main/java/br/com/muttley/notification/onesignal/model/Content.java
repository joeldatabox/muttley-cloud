package br.com.muttley.notification.onesignal.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "language")
public class Content {
    private MuttleyLanguage language;
    private String content;

    public Content() {
    }

    public Content(MuttleyLanguage language, String content) {
        this.language = language;
        this.content = content;
    }
}
