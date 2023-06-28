package br.com.muttley.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Joel Rodrigues Moreira on 18/06/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class BigDecimalUtils {
    public static BigDecimal newZero() {
        return BigDecimal.ZERO.setScale(15, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal setDefaultValueIfIsnull(final BigDecimal value, final BigDecimal defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static BigDecimal setDefaultScale(final BigDecimal bigDecimal) {
        return setScale(bigDecimal, 15, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal setScale(final BigDecimal bigDecimal, final int scale, final RoundingMode roundingMode) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.setScale(scale, roundingMode);
    }

}
