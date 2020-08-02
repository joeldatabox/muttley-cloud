package br.com.muttley.notification.onesignal.model;

import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.stream.Stream;

/**
 * @author Joel Rodrigues Moreira on 02/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public enum MuttleyLanguage {
    English("en"),
    Any("en"),
    Arabic("ar"),
    Catalan("ca"),
    ChineseSimplified("zh-Hans"),
    ChineseTraditional("zh-Hant"),
    Croatian("hr"),
    Czech("cs"),
    Danish("da"),
    Dutch("nl"),
    Estonian("et"),
    Finnish("fi"),
    French("fr"),
    Georgian("ka"),
    Bulgarian("bg"),
    German("de"),
    Greek("el"),
    Hindi("hi"),
    Hebrew("he"),
    Hungarian("hu"),
    Indonesian("id"),
    Italian("it"),
    Japanese("ja"),
    Korean("ko"),
    Latvian("lv"),
    Lithuanian("lt"),
    Malay("ms"),
    Norwegian("nb"),
    Polish("pl"),
    Persian("fa"),
    Portuguese("pt"),
    Romanian("ro"),
    Russian("ru"),
    Swedish("sv"),
    Serbian("sr"),
    Slovak("sk"),
    Spanish("es"),
    Thai("th"),
    Turkish("tr"),
    Ukrainian("uk"),
    Vietnamese("vi");


    @Getter
    private final String oneSignalValue;

    MuttleyLanguage(final String oneSignalValue) {
        this.oneSignalValue = oneSignalValue;
    }

    public static MuttleyLanguage getLanguage(final String language) {
        if (StringUtils.isEmpty(language)) {
            return null;
        }
        switch (language.toLowerCase()) {
            case "english":
                return English;
            case "any":
                return Any;
            case "arabic":
                return Arabic;
            case "catalan":
                return Catalan;
            case "chinesesimplified":
                return ChineseSimplified;
            case "chinesetraditional":
                return ChineseTraditional;
            case "croatian":
                return Croatian;
            case "czech":
                return Czech;
            case "danish":
                return Danish;
            case "dutch":
                return Dutch;
            case "estonian":
                return Estonian;
            case "finnish":
                return Finnish;
            case "french":
                return French;
            case "georgian":
                return Georgian;
            case "bulgarian":
                return Bulgarian;
            case "german":
                return German;
            case "greek":
                return Greek;
            case "hindi":
                return Hindi;
            case "hebrew":
                return Hebrew;
            case "hungarian":
                return Hungarian;
            case "indonesian":
                return Indonesian;
            case "italian":
                return Italian;
            case "japanese":
                return Japanese;
            case "korean":
                return Korean;
            case "latvian":
                return Latvian;
            case "lithuanian":
                return Lithuanian;
            case "malay":
                return Malay;
            case "norwegian":
                return Norwegian;
            case "polish":
                return Polish;
            case "persian":
                return Persian;
            case "portuguese":
                return Portuguese;
            case "romanian":
                return Romanian;
            case "russian":
                return Russian;
            case "swedish":
                return Swedish;
            case "serbian":
                return Serbian;
            case "slovak":
                return Slovak;
            case "spanish":
                return Spanish;
            case "thai":
                return Thai;
            case "turkish":
                return Turkish;
            case "ukrainian":
                return Ukrainian;
            case "vietnamese":
                return Vietnamese;
            default:
                return Stream.of(MuttleyLanguage.values())
                        .filter(it -> it.name().toLowerCase().equals(language.toLowerCase()) || it.oneSignalValue.toLowerCase().equals(language.toLowerCase()))
                        .findFirst()
                        .orElse(null);
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
