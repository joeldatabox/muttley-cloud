package br.com.muttley.model.mercator;


import br.com.muttley.model.mercator.jackson.GeoJsonDeserialize;
import br.com.muttley.model.mercator.jackson.GeoJsonPolygonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.mongodb.core.geo.GeoJson;

import java.util.ArrayList;
import java.util.List;

import static br.com.muttley.model.mercator.TypeGeoJson.POLYGON;
import static java.util.Collections.unmodifiableList;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@JsonSerialize(using = GeoJsonPolygonSerializer.class)
@JsonDeserialize(using = GeoJsonDeserialize.class)
public class GeoJsonPolygon implements GeoJson<List<GeoJsonLineString>> {
    private static final TypeGeoJson TYPE = POLYGON;

    protected final List<GeoJsonLineString> coordinates = new ArrayList<GeoJsonLineString>();

    public GeoJsonPolygon() {
    }

    public GeoJsonPolygon(final Point first, final Point second, final Point third, final Point fourth, final Point... others) {
        this.coordinates.add(new GeoJsonLineString(first, second, third, fourth).add(others));
    }

    public GeoJsonPolygon(final List<GeoJsonLineString> co) {
        this.coordinates.addAll(co);
    }

    @Override
    public String getType() {
        return TYPE.getType();
    }

    @Override
    public List<GeoJsonLineString> getCoordinates() {
        return unmodifiableList(this.coordinates);
    }
}
