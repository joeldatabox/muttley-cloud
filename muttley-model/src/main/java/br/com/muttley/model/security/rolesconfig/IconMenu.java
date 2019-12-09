package br.com.muttley.model.security.rolesconfig;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Joel Rodrigues Moreira on 06/12/19.
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

    public String getIcon() {
        if (fontSet != null && icon != null) {
            if (fontSet.equals(FontSet.FONT_AWESOME)) {
                return this.icon.startsWith("fa-") ? this.icon : "fa-" + this.icon;
            } else if (fontSet.equals(FontSet.MDI)) {
                return this.icon.startsWith("mdi-") ? this.icon : "mdi-" + this.icon;
            }
        }
        return icon;
    }

    public FontSet getFontSet() {
        return fontSet;
    }
}
