package br.com.muttley.model.security.model.enumeration;


public enum Authorities {
    ROLE_USER {
        @Override
        public String getDescription() {
            return "ROLE_USER";
        }
    },
    ROLE_ADMIN {
        @Override
        public String getDescription() {
            return "ROLE_ADMIN";
        }
    };

    public abstract String getDescription();
}
