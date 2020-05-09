package br.com.muttley.model.security.rolesconfig;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Joel Rodrigues Moreira on 09/05/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
public class IconMenu {
    private String icon;
    private FontSet fontSet;

    public IconMenu(final String icon) {
        this(icon, null);
    }

    public IconMenu(final String icon, final FontSet fontSet) {
        this.icon = icon;
        this.fontSet = fontSet;
    }
}
