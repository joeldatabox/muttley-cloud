package br.com.muttley.muttleydiscoveryserver;

import br.com.muttley.model.security.Password;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.impl.TextCodec.BASE64;
import static io.jsonwebtoken.impl.crypto.MacProvider.generateKey;

/*@RunWith(SpringRunner.class)
@SpringBootTest*/
public class MuttleyDiscoveryServerApplicationTests {
    private static final String TIME24HOURS_PATTERN = "(([+-]|)([01]?[0-9]|2[0-3]):[0-5][0-9])|(([+-]|)([01]?[0-9]|2[0-3])([0-5][0-9]))";

    @Test
    public void contextLoads() throws ParseException {
        System.out.println( BASE64.encode(generateKey(HS512).getEncoded()));
        System.out.println( new Password(null, null, "12345", new Date(), null , null, null).
                setPassword("12345").getPassword());
    }

}
