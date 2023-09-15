package br.com.muttley.model.mercator;

import br.com.muttley.model.mercator.jackson.PointDeserializer;
import br.com.muttley.model.mercator.jackson.PointSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static br.com.muttley.utils.TimeZoneUtils.getTimezoneFromId;
import static lombok.AccessLevel.NONE;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
@JsonSerialize(using = PointSerializer.class)
@JsonDeserialize(using = PointDeserializer.class)
public class Point implements Comparable<Point> {
    @NotNull(message = "informe um valor válido")
    protected Double longitude;
    @NotNull(message = "informe um valor válido")
    protected Double latitude;
    protected Double elevation;
    protected ZonedDateTime timeStamp;
    @Setter(NONE)
    protected Long index;//variavel para controle de indice
    @JsonIgnore
    @Transient
    @Setter(NONE)
    private String dateAsString;//variavel para controle interno

    public Point(final Double longitude, final Double latitude, final Double elevation, final ZonedDateTime timeStamp, final Long index) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.elevation = elevation;
        this.timeStamp = timeStamp;
        this.index = index;
        this.setDateAsString(this.getTimeStamp());
    }

    public Point(final Double longitude, final Double latitude, final Double elevation, final ZonedDateTime timeStamp) {
        this(longitude, latitude, elevation, timeStamp, null);
    }

    public Point(final Double longitude, final Double latitude, final Double elevation) {
        this(longitude, latitude, elevation, null);
    }

    public Point(final Double longitude, final Double latitude) {
        this(longitude, latitude, null);
    }

    public Point(final Point point) {
        if (point != null) {
            this.longitude = point.getLongitude();
            this.latitude = point.getLatitude();
            this.elevation = point.getElevation();
            this.timeStamp = point.getTimeStamp();
            this.index = point.getIndex();
            this.setDateAsString(point.getTimeStamp());
        } else {
            this.setDateAsString(null);
        }

    }

    private Point setDateAsString(final ZonedDateTime date) {
        this.dateAsString = date == null ? "" : date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return this;
    }

    public Object[] toArray() {
        return new Object[]{this.longitude, this.latitude, this.elevation, this.getTimeStamp()};
    }

    @JsonIgnore
    public String getZoneFromtimeStamp() {
        if (this.timeStamp == null) {
            return null;
        }
        return getTimezoneFromId(this.timeStamp.getOffset().getId());
    }

    @JsonIgnore
    public boolean containsTimeStamp() {
        return this.timeStamp != null;
    }

    @Override
    public int compareTo(final Point point) {
        return this.getTimeStamp().compareTo(point.getTimeStamp());
    }
}
