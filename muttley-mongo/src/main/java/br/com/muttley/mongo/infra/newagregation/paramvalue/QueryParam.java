package br.com.muttley.mongo.infra.newagregation.paramvalue;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

/**
 * @author Joel Rodrigues Moreira on 02/02/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface QueryParam {
    String getKey();

    String getValue();

    boolean isArrayValue();

    public static class Builder {
        private String key;
        private String value;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder withKey(final String key) {
            this.key = key;
            return this;
        }

        public Builder withValue(final String value) {
            this.value = value;
            return this;
        }

        public QueryParam build() {
            return new QueryParamImpl(this.key, this.value);
        }
    }

    public static class BuilderFromURL {
        private String url;

        private BuilderFromURL() {
        }

        public static BuilderFromURL newInstance() {
            return new BuilderFromURL();
        }

        public BuilderFromURL fromURL(final String url) {
            this.url = url;
            return this;
        }

        public List<QueryParam> build() {
            String[] urlParts = this.url.split("\\?");
            if (urlParts.length > 1) {
                /*String query = urlParts[1];
                for (String param : query.split("&")) {
                    params.add(new QueryParamImpl(param));
                }*/

                return Stream.of(urlParts[1].split("&"))
                        .map(it -> new QueryParamImpl(it))
                        .collect(toCollection(LinkedList::new));
            }
            return new LinkedList<>();
        }
    }
}
