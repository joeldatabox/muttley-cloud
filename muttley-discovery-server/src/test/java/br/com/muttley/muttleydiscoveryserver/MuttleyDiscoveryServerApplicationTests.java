package br.com.muttley.muttleydiscoveryserver;

import org.junit.Test;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/*@RunWith(SpringRunner.class)
@SpringBootTest*/
public class MuttleyDiscoveryServerApplicationTests {
    private static final String TIME24HOURS_PATTERN = "(([+-]|)([01]?[0-9]|2[0-3]):[0-5][0-9])|(([+-]|)([01]?[0-9]|2[0-3])([0-5][0-9]))";

    @Test
    public void contextLoads() {

        Pattern pattern = Pattern.compile(TIME24HOURS_PATTERN);

        System.out.println(pattern.matcher("-2360").matches());

    }

}
