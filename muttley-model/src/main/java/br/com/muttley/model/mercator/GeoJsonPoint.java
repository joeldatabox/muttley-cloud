package br.com.muttley.model.mercator;

import br.com.muttley.model.mercator.jackson.GeoJsonDeserialize;
import br.com.muttley.model.mercator.jackson.GeoJsonPointSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.geo.GeoJson;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static br.com.muttley.model.mercator.TypeGeoJson.POINT;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

@Getter
@Setter
@Accessors(chain = true)
@JsonSerialize(using = GeoJsonPointSerializer.class)
@JsonDeserialize(using = GeoJsonDeserialize.class)
public class GeoJsonPoint extends Point implements GeoJson<List<Double>> {
    private static final TypeGeoJson TYPE = POINT;

    public GeoJsonPoint(final double longitude, final double latitude, final double elevation, final ZonedDateTime timeStamp) {
        super(longitude, latitude, elevation, timeStamp);
    }

    public GeoJsonPoint(final double longitude, final double latitude, final double elevation) {
        super(longitude, latitude, elevation);
    }

    public GeoJsonPoint(final double longitude, final double latitude) {
        super(longitude, latitude);
    }

    public GeoJsonPoint(final Point point) {
        super(point);
    }

    @Override
    public String getType() {
        return TYPE.getType();
    }

    @Override
    public List<Double> getCoordinates() {
        return Arrays.asList(Double.valueOf(getLongitude()), Double.valueOf(getLatitude()));
    }

    public Point toPoit() {
        return new Point(this.getLongitude(), this.getLatitude(), this.getElevation(), this.getTimeStamp());
    }
}
