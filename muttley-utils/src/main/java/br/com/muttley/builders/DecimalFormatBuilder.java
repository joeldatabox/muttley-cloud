package br.com.muttley.builders;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author Joel Rodrigues Moreira on 28/06/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Setter
@Accessors(chain = true)
public class DecimalFormatBuilder {
    private Locale locale;
    private Character decimalSeparator = ',';
    private Character groupingSeparator = '.';
    private String currencySymbol;
    private Character digit;
    private String exponentSeparator;


    public DecimalFormatBuilder() {
    }

    public DecimalFormat build(final String pattern) {
        final DecimalFormatSymbols symbols = this.locale != null ? new DecimalFormatSymbols(this.locale) : new DecimalFormatSymbols();
        if (this.currencySymbol != null) {
            symbols.setCurrencySymbol(this.currencySymbol);
        }
        if (this.digit != null) {
            symbols.setDigit(this.digit);
        }
        if (this.exponentSeparator != null) {
            symbols.setExponentSeparator(this.exponentSeparator);
        }
        if (this.groupingSeparator != null) {
            symbols.setGroupingSeparator(this.groupingSeparator);
        }
        symbols.setDecimalSeparator(this.decimalSeparator);
        symbols.setGroupingSeparator(this.groupingSeparator);
        return new DecimalFormat(pattern, symbols);
    }

}
