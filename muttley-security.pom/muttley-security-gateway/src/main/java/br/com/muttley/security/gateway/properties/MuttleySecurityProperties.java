package br.com.muttley.security.gateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Joel Rodrigues Moreira on 30/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@ConfigurationProperties(prefix = MuttleySecurityProperties.PREFIX)
public class MuttleySecurityProperties {
    protected static final String PREFIX = "muttley";
    public static final String LOGIN_END_POINT = "${muttley.security.jwt.controller.login-end-point:/api/auth/login}";
    public static final String REFRESH_END_POINT = "${muttley.security.jwt.controller.refresh-end-point:/api/auth/refresh}";
    public static final String CREATE_END_POINT = "${muttley.security.jwt.controller.create-end-point:/api/auth/register}";
    public static final String MANAGER_USER_END_POINT = "${muttley.security.jwt.controller.manager-user-end-point:/api/auth/manager}";
    public static final String TOKEN_HEADER = "${muttley.security.jwt.controller.token-header:Authorization}";
    private Security security = new Security();
    private SecurityServer securityServer = new SecurityServer();

    public Security getSecurity() {
        return security;
    }

    public MuttleySecurityProperties setSecurity(Security security) {
        this.security = security;
        return this;
    }

    public SecurityServer getSecurityServer() {
        return securityServer;
    }

    public MuttleySecurityProperties setSecurityServer(SecurityServer securityServer) {
        this.securityServer = securityServer;
        return this;
    }

    public static class SecurityServer {
        private User user = new User();
        private String nameServer;

        public User getUser() {
            return user;
        }

        public SecurityServer setUser(User user) {
            this.user = user;
            return this;
        }

        public String getNameServer() {
            return nameServer;
        }

        public SecurityServer setNameServer(String nameServer) {
            this.nameServer = nameServer;
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
                private String loginEndPoint = "/api/auth/login";
                private String refreshEndPoint = "/api/auth/refresh";
                private String tokenHeader = "Authorization";
                private String tokenHeaderJwt = "Authorization-jwt";
                private String createEndPoint = "/api/auth/register";
                private String managerUserEndPoint = "/api/auth/manager";

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

                public String getRefreshEndPoint() {
                    return refreshEndPoint;
                }

                public Controller setRefreshEndPoint(String refreshEndPoint) {
                    this.refreshEndPoint = refreshEndPoint;
                    return this;
                }

                public String getCreateEndPoint() {
                    return createEndPoint;
                }

                public Controller setCreateEndPoint(String createEndPoint) {
                    this.createEndPoint = createEndPoint;
                    return this;
                }

                public String getManagerUserEndPoint() {
                    return managerUserEndPoint;
                }

                public Controller setManagerUserEndPoint(String managerUserEndPoint) {
                    this.managerUserEndPoint = managerUserEndPoint;
                    return this;
                }
            }
        }
    }
}
