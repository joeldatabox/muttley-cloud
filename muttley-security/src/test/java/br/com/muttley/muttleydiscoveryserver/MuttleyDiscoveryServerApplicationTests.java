package br.com.muttley.muttleydiscoveryserver;

import br.com.muttley.model.security.Password;
import com.sun.org.apache.xpath.internal.operations.String;
import org.apache.commons.lang.CharEncoding;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.impl.TextCodec.BASE64;
import static io.jsonwebtoken.impl.crypto.MacProvider.generateKey;

/*@RunWith(SpringRunner.class)
@SpringBootTest*/
public class MuttleyDiscoveryServerApplicationTests {
   // private static final String TIME24HOURS_PATTERN = "(([+-]|)([01]?[0-9]|2[0-3]):[0-5][0-9])|(([+-]|)([01]?[0-9]|2[0-3])([0-5][0-9]))";

    @Test
    public void contextLoads() throws ParseException {
        final java.lang.String s = new java.lang.String(generateKey(HS256).getEncoded(), StandardCharsets.UTF_8);
        System.out.println(s);
        System.out.println(BASE64.encode(generateKey(HS512).getEncoded()));
        //System.out.println(new Password(null, null, "12345", new Date(), null, null, null).
             //   setPassword("12345").getPassword());

        System.out.println(Password.BuilderPasswordEncoder.getPasswordEncoder().encode("123456"));
    }

}
