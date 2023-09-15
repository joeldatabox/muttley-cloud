package br.com.muttley.utils;

import org.springframework.util.StringUtils;

/**
 * @author Joel Rodrigues Moreira on 22/12/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class StringBuilderUtils {
    public static StringBuilder append(final StringBuilder builder, final StringBuilder content) {
        if (!StringUtils.isEmpty(content)) {
            if (builder.length() > 0) {
                builder.append(", ").append(content);
            } else {
                builder.append(content);
            }
        }
        return builder;
    }

    public static StringBuilder append(final StringBuilder builder, final String content) {
        if (!StringUtils.isEmpty(content)) {
            if (builder.length() > 0) {
                builder.append(", ").append(content);
            } else {
                builder.append(content);
            }
        }
        return builder;
    }

}
