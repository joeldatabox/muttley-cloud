package br.com.muttley.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperReport;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira 10/11/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MuttleyReport {

    InputStream getSourceReport();

    JasperReport loadReport();

    Map<String, Object> getParams();

    void print(final OutputStream outputStream);

    JRDataSource getDataSource();
}
