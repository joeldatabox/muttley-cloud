package br.com.muttley.model.hermes.notification.onesignal;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.model.hermes.notification.TokenId;
import br.com.muttley.model.hermes.notification.UserTokensNotification;
import br.com.muttley.model.hermes.notification.onesignal.jackson.CollectionContentSerialize;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.springframework.util.StringUtils.isEmpty;


/**
 * @author Joel Rodrigues Moreira on 02/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "appId")
public class Notification implements Cloneable {
    @NotBlank
    @JsonProperty("app_id")
    private String appId;

    @NotEmpty
    @Valid
    @JsonProperty("include_player_ids")
    private Set<String> players;

    @JsonProperty("big_picture")
    private String picture;

    @Valid
    private NotificationData data;

    @Valid
    @JsonSerialize(using = CollectionContentSerialize.class)
    private Set<Content> contents;

    @NotEmpty
    @Valid
    @JsonSerialize(using = CollectionContentSerialize.class)
    private Set<Content> headings;

    @Valid
    @JsonSerialize(using = CollectionContentSerialize.class)
    private Set<Content> subtitle;

    public Notification() {
        this.players = new HashSet<>();
        this.contents = new HashSet<>();
        this.headings = new HashSet<>();
        this.subtitle = new HashSet<>();
    }

    public Notification addContents(final Collection<Content> contents) {
        if (!CollectionUtils.isEmpty(contents)) {
            this.contents.addAll(contents.stream().filter(it -> !isEmpty(it)).collect(Collectors.toSet()));
        }
        return this;
    }

    public Notification addContent(final Content... contents) {
        return this.addContents(asList(contents));
    }

    public Notification addHeadings(final Collection<Content> headings) {
        if (!CollectionUtils.isEmpty(headings)) {
            this.headings.addAll(headings.stream().filter(it -> !isEmpty(it)).collect(Collectors.toSet()));
        }
        return this;
    }

    public Notification addHeadings(final Content... headings) {
        this.addHeadings(asList(headings));
        return this;
    }

    public Notification addSubtitles(final Collection<Content> subtitles) {
        if (!CollectionUtils.isEmpty(subtitles)) {
            this.subtitle.addAll(subtitles.stream().filter(it -> !isEmpty(it)).collect(Collectors.toSet()));
        }
        return this;
    }

    public Notification addSubtitles(final Content... subtitles) {
        this.addSubtitles(asList(subtitles));
        return this;
    }

    public Notification addPlayers(final Collection<String> players) {
        if (!CollectionUtils.isEmpty(players)) {
            this.players.addAll(players.stream().filter(it -> !isEmpty(it)).collect(Collectors.toSet()));
        }
        return this;
    }

    public Notification addPlayers(final String... player) {
        return this.addPlayers(asList(player));
    }


    public Notification addPlayers(final UserTokensNotification userTokensNotification) {
        if (userTokensNotification != null) {
            return this.addPlayers(userTokensNotification.getTokens());
        }
        return this;
    }

    public Notification addPlayers(final Set<TokenId> tokens) {
        if (tokens != null) {
            return this.addPlayers(tokens.stream().map(TokenId::getToken).collect(Collectors.toSet()));
        }
        return this;
    }

    @Override
    public Notification clone() {
        try {
            return (Notification) super.clone();
        } catch (final CloneNotSupportedException ex) {
            throw new MuttleyBadRequestException(ex);
        }
    }
}
