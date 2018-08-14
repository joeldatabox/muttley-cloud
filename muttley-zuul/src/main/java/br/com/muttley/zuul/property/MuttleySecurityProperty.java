package br.com.muttley.zuul.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MuttleySecurityProperty.PREFIX)
public class MuttleySecurityProperty {
    protected static final String PREFIX = "muttley.security";
    private Jwt jwt;

    public MuttleySecurityProperty() {
        this.jwt = new Jwt();
    }

    public Jwt getJwt() {
        return jwt;
    }

    public MuttleySecurityProperty setJwt(Jwt jwt) {
        this.jwt = jwt;
        return this;
    }

    public static class Jwt {
        private Controller controller;

        public Jwt() {
            this.controller = new Controller();
        }

        public Controller getController() {
            return controller;
        }

        public Jwt setController(Controller controller) {
            this.controller = controller;
            return this;
        }

        public static class Controller {
            private String tokenHeader;
            private String tokenHeaderJwt;

            public Controller() {
                this.tokenHeader = "Authorization";
                this.tokenHeaderJwt = "Authorization-jwt";
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
        }
    }
}
