package br.com.muttley.notification.onesignal.model;

import br.com.muttley.notification.onesignal.model.jackson.CollectionContentSerialize;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 02/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class Notification {
    @JsonProperty("app_id")
    private String appId;
    @JsonProperty("include_player_ids")
    private Set<String> players;
    private Object data;
    @JsonProperty("big_picture")
    private String picture;

    @JsonSerialize(using = CollectionContentSerialize.class)
    private Set<Content> contents;
    @JsonSerialize(using = CollectionContentSerialize.class)
    private Set<Content> headings;
    @JsonSerialize(using = CollectionContentSerialize.class)
    private Set<Content> subtitle;

    public Notification() {
        this.contents = new HashSet<>();
        this.headings = new HashSet<>();
        this.subtitle = new HashSet<>();
    }

    public Notification addContent(final Content content) {
        this.contents.add(content);
        return this;
    }

    public Notification addHeading(final Content heading) {
        this.headings.add(heading);
        return this;
    }

    public Notification addSubtitle(final Content subtitle) {
        this.subtitle.add(subtitle);
        return this;
    }
}
