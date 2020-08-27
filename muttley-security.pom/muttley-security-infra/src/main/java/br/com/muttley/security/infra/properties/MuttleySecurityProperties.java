package br.com.muttley.security.infra.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Joel Rodrigues Moreira on 26/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@ConfigurationProperties(prefix = MuttleySecurityProperties.PREFIX)
@Getter
@Setter
@Accessors(chain = true)
public class MuttleySecurityProperties {
    @Getter(AccessLevel.NONE)
    protected static final String PREFIX = "muttley";

    public static final String LOGIN_END_POINT = "${muttley.security-server.security.jwt.controller.login-end-point:/api/auth/login}";
    public static final String REFRESH_END_POINT = "${muttley.security-server.security.jwt.controller.refresh-end-point:/api/auth/refresh}";
    public static final String CREATE_END_POINT = "${muttley.security-server.security.jwt.controller.create-end-point:/api/auth/register}";
    public static final String MANAGER_USER_END_POINT = "${muttley.security-server.security.jwt.controller.manager-user-end-point:/api/auth/manager}";
    public static final String TOKEN_HEADER = "${muttley.security-server.security.jwt.controller.token-header:Authorization}";
    public static final String TOKE_EXPIRATION = "${muttley.security-server.security.jwt.token.expiration:3600000}";
    public static final String TOKEN_HEADER_JWT = "${muttley.security-server.security.jwt.controller.token-header:Authorization}";


    private SecurityServer securityServer = new SecurityServer();


    @Getter
    @Setter
    @Accessors(chain = true)
    public static class SecurityServer {
        private Security security = new Security();
        private String nameServer;

        @Getter
        @Setter
        @Accessors(chain = true)
        public static class Security {
            private Jwt jwt = new Jwt();
            private User user = new User();

            @Getter
            @Setter
            @Accessors(chain = true)
            public static class Jwt {
                private Controller controller = new Controller();
                private Token token = new Token();

                @Getter
                @Setter
                @Accessors(chain = true)
                public static class Token {
                    //por padrao será apenas uma hora
                    private Integer expiration = 3600000;
                }

                @Getter
                @Setter
                @Accessors(chain = true)
                public static class Controller {
                    private String loginEndPoint = "/api/auth/login";
                    private String refreshEndPoint = "/api/auth/refresh";
                    private String tokenHeader = "Authorization";
                    private String tokenHeaderJwt = "Authorization-jwt";
                    private String createEndPoint = "/api/auth/register";
                    private String managerUserEndPoint = "/api/auth/manager";
                }
            }

            @Getter
            @Setter
            @Accessors(chain = true)
            public static class User {
                private String name = "muttley";
                private String password = "muttley";
                private String role = "SYSTEM";
            }
        }
    }
}
