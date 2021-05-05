package br.com.muttley.model.mercator;


import br.com.muttley.model.mercator.jackson.GeoJsonDeserialize;
import br.com.muttley.model.mercator.jackson.GeoJsonMultiPointSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static br.com.muttley.model.mercator.TypeGeoJson.MULTIPOINT;
import static java.util.Collections.unmodifiableList;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
@JsonSerialize(using = GeoJsonMultiPointSerializer.class)
@JsonDeserialize(using = GeoJsonDeserialize.class)
public class GeoJsonMultiPoint implements GeoJson<Iterable<Point>> {
    private static final TypeGeoJson TYPE = MULTIPOINT;

    protected final List<Point> coordinates;

    public GeoJsonMultiPoint() {
        this.coordinates = new ArrayList<>();
    }

    public GeoJsonMultiPoint(final List<Point> coordinates) {
        this.coordinates = coordinates;
    }

    public GeoJsonMultiPoint(final Point first, final Point second, final Point... others) {
        this.coordinates = new ArrayList<>();
        this.coordinates.add(first);
        this.coordinates.add(second);
        this.coordinates.addAll(Arrays.asList(others));
    }

    public GeoJsonMultiPoint add(final Point... point) {
        this.coordinates.addAll(Arrays.asList(point));
        return this;
    }

    public GeoJsonMultiPoint add(final Collection<? extends Point> points) {
        this.coordinates.addAll(points);
        return this;
    }

    public List<Point> getPoints() {
        return unmodifiableList(this.coordinates);
    }

    @Override
    public String getType() {
        return TYPE.getType();
    }

    @Override
    public Iterable<Point> getCoordinates() {
        return unmodifiableList(this.coordinates);
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(this.coordinates);
    }

    public Point getCoordinateByIndex(final Long index) {
        return this.coordinates.stream().filter(it -> it.getIndex() == index).findFirst().orElse(null);
    }
}
