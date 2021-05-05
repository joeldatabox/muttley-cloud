package br.com.muttley.model.mercator;

import br.com.muttley.model.mercator.jackson.GeoJsonDeserialize;
import br.com.muttley.model.mercator.jackson.GeoJsonMultiLineStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.mongodb.core.geo.GeoJson;

import java.util.ArrayList;
import java.util.List;

import static br.com.muttley.model.mercator.TypeGeoJson.MULTILINESTRING;
import static java.util.Collections.unmodifiableList;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@JsonSerialize(using = GeoJsonMultiLineStringSerializer.class)
@JsonDeserialize(using = GeoJsonDeserialize.class)
public class GeoJsonMultiLineString implements GeoJson<Iterable<GeoJsonLineString>> {
    private static final TypeGeoJson TYPE = MULTILINESTRING;

    protected List<GeoJsonLineString> coordinates = new ArrayList<>();

    public GeoJsonMultiLineString(List<Point>... lines) {
        for (List<Point> line : lines) {
            this.coordinates.add(new GeoJsonLineString(line));
        }
    }

    public GeoJsonMultiLineString(List<GeoJsonLineString> lines) {
        this.coordinates.addAll(lines);
    }

    @Override
    public String getType() {
        return TYPE.getType();
    }

    @Override
    public Iterable<GeoJsonLineString> getCoordinates() {
        return unmodifiableList(this.coordinates);
    }
}
