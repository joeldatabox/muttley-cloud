package br.com.muttley.model.mercator;

import java.util.stream.Stream;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public enum TypeGeoJson {
    POINT("Point"),
    MULTIPOINT("MultiPoint"),
    LINESTRING("LineString"),
    MULTILINESTRING("MultiLineString"),
    POLYGON("Polygon"),
    MULTIPOLYGON("MultiPolygon");

    private final String type;

    TypeGeoJson(final String type) {
        this.type = type;
    }

    public static TypeGeoJson of(final String value) {
        return Stream.of(TypeGeoJson.values())
                .filter(it -> it.getType().equals(value) || it.name().equals(value))
                .findFirst()
                .orElse(null);
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return getType();
    }
}
