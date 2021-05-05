package br.com.muttley.model.mercator;

import br.com.muttley.model.mercator.jackson.GeoJsonDeserialize;
import br.com.muttley.model.mercator.jackson.GeoJsonLineStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Arrays;
import java.util.List;

import static br.com.muttley.model.mercator.TypeGeoJson.LINESTRING;


/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@JsonSerialize(using = GeoJsonLineStringSerializer.class)
@JsonDeserialize(using = GeoJsonDeserialize.class)
public class GeoJsonLineString extends GeoJsonMultiPoint {
    private static final TypeGeoJson TYPE = LINESTRING;

    public GeoJsonLineString() {
        super();
    }

    public GeoJsonLineString(final List<Point> points) {
        super(points);
    }

    public GeoJsonLineString(final Point first, final Point second, final Point... others) {
        super(first, second, others);
    }

    public GeoJsonLineString add(final Point... point) {
        super.coordinates.addAll(Arrays.asList(point));
        return this;
    }

    public GeoJsonLineString add(final List<Point> points) {
        super.coordinates.addAll(points);
        return this;
    }

    @Override
    public String getType() {
        return TYPE.getType();
    }
}
