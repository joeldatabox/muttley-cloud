package br.com.muttley.model.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Base64.getDecoder;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
public class JwtToken implements Serializable {
    @JsonIgnore
    private Map<String, String> payload;

    private final String token;

    @JsonCreator
    public JwtToken(@JsonProperty("token") final String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return isNullOrEmpty(this.token);
    }

    @JsonIgnore
    public long getExpiration() {
        decodeToken();
        return Long.valueOf(String.valueOf(payload.get("exp")));
    }

    @JsonIgnore
    public String getUsername() {
        decodeToken();
        return payload.get("sub");
    }

    private void decodeToken() {

        if (payload == null) {
            try {
                //separando os blocos de informações
                final String[] blocksEncond = this.token.split("\\.");
                //array com json de informações do token
                final String[] paylodString = new String[]{new String(getDecoder().decode(blocksEncond[0]), "UTF-8"), new String(getDecoder().decode(blocksEncond[1]), "UTF-8")};
                //deserializando
                final ObjectMapper mapper = new ObjectMapper();

                payload = mapper.readValue(paylodString[0], Map.class);
                payload.putAll(mapper.readValue(paylodString[1], Map.class));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
