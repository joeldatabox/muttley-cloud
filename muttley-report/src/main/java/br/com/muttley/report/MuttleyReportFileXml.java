package br.com.muttley.report;

import java.io.File;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Joel Rodrigues Moreira on 28/02/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MuttleyReportFileXml {
    private final String resource;
    protected File createdTmpFile;
    private final MuttleyReportFileXml[] subReports;

    public MuttleyReportFileXml(final String resource) {
        this(resource, (String) null);
    }

    public MuttleyReportFileXml(final String resource, String... subReports) {
        this.resource = resource;
        if (subReports != null) {
            this.subReports = Stream.of(subReports).map(it -> new MuttleyReportFileXml(it)).toArray(MuttleyReportFileXml[]::new);
        } else {
            this.subReports = null;
        }
    }

    public MuttleyReportFileXml(final String resource, MuttleyReportFileXml... subReports) {
        this.resource = resource;
        this.subReports = subReports;
    }

    public String getResource() {
        return resource;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MuttleyReportFileXml)) return false;
        final MuttleyReportFileXml that = (MuttleyReportFileXml) o;
        return Objects.equals(getResource(), that.getResource());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getResource());
    }
}
