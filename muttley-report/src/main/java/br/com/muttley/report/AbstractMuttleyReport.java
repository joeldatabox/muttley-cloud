package br.com.muttley.report;

import br.com.muttley.exception.throwables.MuttleyException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import static java.lang.System.getProperty;
import static net.sf.jasperreports.engine.JRParameter.REPORT_VIRTUALIZER;
import static net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfStream;
import static net.sf.jasperreports.engine.JasperFillManager.fillReport;
import static net.sf.jasperreports.engine.util.JRLoader.loadObject;

/**
 * @author Joel Rodrigues Moreira 10/11/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractMuttleyReport implements MuttleyReport {
    protected final AbstractMuttleyReportBuilder builder;

    protected AbstractMuttleyReport(final AbstractMuttleyReportBuilder builder) {
        this.builder = builder;
    }

    @Override
    public InputStream getSourceReport() {
        return this.getClass().getResourceAsStream(this.builder.getFileReport());
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
        //criando o virtualizador de cache para impressão
        final JRSwapFileVirtualizer virtualizer = new JRSwapFileVirtualizer(
                1,
                new JRSwapFile(getProperty("java.io.tmpdir"), 1, 100),
                true
        );
        try {
            //adicionando o virtualizador
            final Map<String, Object> map = this.getParams();
            map.put(REPORT_VIRTUALIZER, virtualizer);

            exportReportToPdfStream(
                    fillReport(
                            this.loadReport(),
                            map,
                            this.getDataSource()
                    ),
                    outputStream
            );
        } catch (final JRException e) {
            throw new MuttleyException(e);
        } finally {
            virtualizer.cleanup();
        }
    }

}
