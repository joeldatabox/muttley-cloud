package br.com.muttley.report;

import br.com.muttley.exception.throwables.MuttleyException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import static net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream;
import static net.sf.jasperreports.engine.JasperFillManager.fillReport;
import static net.sf.jasperreports.engine.util.JRLoader.loadObject;

/**
 * @author Joel Rodrigues Moreira on 21/07/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractMuttleyReport implements MuttleyReport {
    protected final AbstractMuttleyReportBuilder builder;

    protected AbstractMuttleyReport(final AbstractMuttleyReportBuilder builder) {
        this.builder = builder;
    }

    @Override
    public String getFileForSubReport() {
        return null;
    }

    @Override
    public InputStream getSourceReport() {
        return this.getClass().getResourceAsStream(this.getFileReport());
    }

    @Override
    public JasperReport loadReport() {
        try {
            return (JasperReport) loadObject(getSourceReport());
        } catch (final JRException ex) {
            throw new MuttleyException(ex);
        }
    }

    @Override
    public Map<String, Object> getParams() {
        return builder.getParams();
    }

    @Override
    public void print(final OutputStream outputStream) {
        try {
            exportReportToPdfStream(
                    fillReport(
                            this.loadReport(),
                            this.getParams(),
                            this.getDataSource()
                    ),
                    outputStream
            );
        } catch (final JRException e) {
            throw new MuttleyException(e);
        }
    }

}
