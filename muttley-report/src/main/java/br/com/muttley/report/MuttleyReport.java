package br.com.muttley.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperReport;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 21/07/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MuttleyReport {
    String getFileForSubReport();

    String getFileReport();

    InputStream getSourceReport();

    JasperReport loadReport();

    Map<String, Object> getParams();

    void print(final OutputStream outputStream);

    JRDataSource getDataSource();
}
