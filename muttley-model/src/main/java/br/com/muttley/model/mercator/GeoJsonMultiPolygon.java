package br.com.muttley.model.mercator;


import br.com.muttley.model.mercator.jackson.GeoJsonDeserialize;
import br.com.muttley.model.mercator.jackson.GeoJsonMultiPolygonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.mongodb.core.geo.GeoJson;

import java.util.ArrayList;
import java.util.List;

import static br.com.muttley.model.mercator.TypeGeoJson.MULTIPOLYGON;
import static java.util.Collections.unmodifiableList;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@EqualsAndHashCode(of = "coordinates")
@JsonSerialize(using = GeoJsonMultiPolygonSerializer.class)
@JsonDeserialize(using = GeoJsonDeserialize.class)
public class GeoJsonMultiPolygon implements GeoJson<Iterable<GeoJsonPolygon>> {
    private static final TypeGeoJson TYPE = MULTIPOLYGON;

    protected List<GeoJsonPolygon> coordinates = new ArrayList<>();

    public GeoJsonMultiPolygon(List<GeoJsonPolygon> polygons) {
        this.coordinates.addAll(polygons);
    }

    @Override
    public String getType() {
        return TYPE.getType();
    }

    @Override
    public Iterable<GeoJsonPolygon> getCoordinates() {
        return unmodifiableList(this.coordinates);
    }
}
