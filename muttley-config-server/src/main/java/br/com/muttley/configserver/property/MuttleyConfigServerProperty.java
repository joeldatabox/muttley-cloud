package br.com.muttley.configserver.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MuttleyConfigServerProperty.PREFIX)
public class MuttleyConfigServerProperty {
    protected static final String PREFIX = "muttley.config-server";
    private Security security = new Security();

    public Security getSecurity() {
        return security;
    }

    public MuttleyConfigServerProperty setSecurity(Security security) {
        this.security = security;
        return this;
    }

    public static class Security {
        private User user = new User();

        public User getUser() {
            return user;
        }

        public Security setUser(User user) {
            this.user = user;
            return this;
        }

        public static class User {
            private String name = "muttley";
            private String password = "muttley";
            private String role = "ROLE_SYSTEM";

            public String getName() {
                return name;
            }

            public User setName(String name) {
                this.name = name;
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
}
