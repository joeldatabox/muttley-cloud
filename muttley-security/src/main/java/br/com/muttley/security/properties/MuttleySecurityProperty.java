package br.com.muttley.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MuttleySecurityProperty.PREFIX)
public class MuttleySecurityProperty {
    protected static final String PREFIX = "muttley";
    private Module module;
    private Security security;
    private SecurityServer securityServer;

    public MuttleySecurityProperty() {
        this.security = new Security();
        this.securityServer = new SecurityServer();
        this.module = Module.CLIENT;
    }

    public Module getModule() {
        return module;
    }

    public MuttleySecurityProperty setModule(Module module) {
        this.module = module;
        return this;
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
        private User user;

        public SecurityServer() {
            this.user = new User();
        }

        public User getUser() {
            return user;
        }

        public SecurityServer setUser(User user) {
            this.user = user;
            return this;
        }

        public static class User {
            private String name;
            private String password;
            private String role;

            public User() {
                this.name = "muttley";
                this.password = "muttley";
                this.role = "SYSTEM";
            }

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
        private Jwt jwt;

        public Security() {
            this.jwt = new Jwt();
        }

        public Jwt getJwt() {
            return jwt;
        }

        public Security setJwt(Jwt jwt) {
            this.jwt = jwt;
            return this;
        }

        public static class Jwt {
            private Controller controller;
            private Token token;

            public Jwt() {
                this.controller = new Controller();
                this.token = new Token();
            }

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
                private Integer expiration;

                public Token() {
                    //por padrao será apenas uma hora
                    this.expiration = 3600000;
                }

                public Integer getExpiration() {
                    return expiration;
                }

                public Token setExpiration(Integer expiration) {
                    this.expiration = expiration;
                    return this;
                }
            }

            public static class Controller {
                private String loginEndPoint;
                private String refreshEndPoint;
                private String tokenHeader;
                private String tokenHeaderJwt;
                private String createEndPoint;
                private String managerUserEndPoint;


                public Controller() {
                    this.tokenHeader = "Authorization";
                    this.tokenHeaderJwt = "Authorization-jwt";
                    this.loginEndPoint = "/api/auth/login";
                    this.refreshEndPoint = "/api/auth/refresh";
                    this.createEndPoint = "/api/auth/register";
                    this.managerUserEndPoint = "/api/auth/manager";
                }

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
