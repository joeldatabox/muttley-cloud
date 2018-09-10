package br.com.muttley.security.server.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MuttleySecurityProperty.PREFIX)
public class MuttleySecurityProperty {
    protected static final String PREFIX = "muttley";
    public static final String TOKEN_HEADER_JWT = "${muttley.security.jwt.controller.token-header-jwt:Authorization-jwt}";
    private Security security = new Security();
    private SecurityServer securityServer = new SecurityServer();

    public MuttleySecurityProperty() {
        this.security = new Security();
        this.securityServer = new SecurityServer();
    }

    public Security getSecurity() {
        return security;
    }

    public MuttleySecurityProperty setSecurity(Security security) {
        this.security = security;
        return this;
    }

    public SecurityServer getSecurityServer() {
        return securityServer;
    }

    public MuttleySecurityProperty setSecurityServer(SecurityServer securityServer) {
        this.securityServer = securityServer;
        return this;
    }

    public static class SecurityServer {
        private User user = new User();

        public User getUser() {
            return user;
        }

        public SecurityServer setUser(User user) {
            this.user = user;
            return this;
        }

        public static class User {
            private String name = "muttley";
            private String password = "muttley";
            private String role = "SYSTEM";

            public String getName() {
                return name;
            }

            public User setName(String user) {
                this.name = user;
                return this;
            }

            public String getPassword() {
                return password;
            }

            public User setPassword(String password) {
                this.password = password;
                return this;
            }

            public String getRole() {
                return role;
            }

            public User setRole(String role) {
                this.role = role;
                return this;
            }
        }
    }

    public static class Security {
        private Jwt jwt = new Jwt();

        public Jwt getJwt() {
            return jwt;
        }

        public Security setJwt(Jwt jwt) {
            this.jwt = jwt;
            return this;
        }

        public static class Jwt {
            private Controller controller = new Controller();
            private Token token = new Token();

            public Controller getController() {
                return controller;
            }

            public Jwt setController(Controller controller) {
                this.controller = controller;
                return this;
            }

            public Token getToken() {
                return token;
            }

            public Jwt setToken(Token token) {
                this.token = token;
                return this;
            }

            public static class Token {
                //por padrao será apenas uma hora
                private Integer expiration = 3600000;

                public Integer getExpiration() {
                    return expiration;
                }

                public Token setExpiration(Integer expiration) {
                    this.expiration = expiration;
                    return this;
                }
            }

            public static class Controller {
                private String tokenHeader = "Authorization";
                private String tokenHeaderJwt = "Authorization-jwt";
                private String loginEndPoint = "/api/auth/login";

                public String getTokenHeader() {
                    return tokenHeader;
                }

                public Controller setTokenHeader(String tokenHeader) {
                    this.tokenHeader = tokenHeader;
                    return this;
                }

                public String getTokenHeaderJwt() {
                    return tokenHeaderJwt;
                }

                public Controller setTokenHeaderJwt(String tokenHeaderJwt) {
                    this.tokenHeaderJwt = tokenHeaderJwt;
                    return this;
                }

                public String getLoginEndPoint() {
                    return loginEndPoint;
                }

                public Controller setLoginEndPoint(String loginEndPoint) {
                    this.loginEndPoint = loginEndPoint;
                    return this;
                }
            }
        }
    }
}
