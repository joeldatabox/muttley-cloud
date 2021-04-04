package br.com.muttley.muttleydiscoveryserver;


import br.com.muttley.muttleydiscoveryserver.autoconfig.MuttleyDiscoveryServerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@SpringBootTest(classes = MuttleyDiscoveryServerConfig.class)
public class MuttleyDiscoveryServerApplicationTests {
    private static final String TIME24HOURS_PATTERN = "(([+-]|)([01]?[0-9]|2[0-3]):[0-5][0-9])|(([+-]|)([01]?[0-9]|2[0-3])([0-5][0-9]))";

    @Test
    public void contextLoads() throws ParseException {
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        final Date date = df.parse("2019-10-14T11:10:32.399-0300");
        final OffsetDateTime offsetDateTime = OffsetDateTime.parse("2019-10-14T11:10:32.399-0500", dateFormatter);
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse("2019-10-14T11:10:32.399-0500", dateFormatter);

        System.out.println("date            " + date);
        System.out.println("offsetDateTime  " + offsetDateTime);
        System.out.println("zonedDateTime   " + zonedDateTime);
        System.out.println("date from offse " + Date.from(offsetDateTime.toInstant()));

        System.out.println(zonedDateTime.getZone());
        System.out.println(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
        System.out.println(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).withZoneSameLocal(ZoneId.of("-0200")));
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println(Date.from(ZonedDateTime.now(ZoneId.of("-0300")).toInstant()));
        System.out.println(ZonedDateTime.ofInstant(Date.from(ZonedDateTime.now().toInstant()).toInstant(), ZoneId.systemDefault()));
        System.out.println(Date.from(ZonedDateTime.ofInstant(Date.from(ZonedDateTime.now().toInstant()).toInstant(), ZoneId.systemDefault()).toInstant()));
        System.out.println(ZonedDateTime.ofInstant(Date.from(ZonedDateTime.now().toInstant()).toInstant(), ZoneId.systemDefault()).getZone());
        System.out.println(ZonedDateTime.ofInstant(Date.from(ZonedDateTime.now().toInstant()).toInstant(), ZoneId.systemDefault()).getOffset());
        //System.out.println("date from local " + localDateTime.toInstant(localDateTime.get()));
        //System.out.println("other           " + OffsetDateTime.from(Date.from(offsetDateTime.toInstant()).toInstant()));
        //System.out.println("other           " + OffsetDateTime.from(Date.from(offsetDateTime.toInstant()).toInstant()).atZoneSimilarLocal(ZoneOffset.of("America/Sao_Paulo")));


    }

}
