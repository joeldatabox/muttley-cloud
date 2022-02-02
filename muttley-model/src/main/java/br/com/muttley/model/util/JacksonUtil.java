package br.com.muttley.model.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Joel Rodrigues Moreira on 02/02/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class JacksonUtil {
    public static String extractString(final JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText();
    }

    public static String extractString(final String path, final JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return extractString(node.get(path));
    }

    public static Long extractLong(final JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asLong();
    }

    public static Long extractLong(final String path, final JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return extractLong(node.get(path));
    }

    public static Integer extractInteger(final JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asInt();
    }

    public static Integer extractInteger(final String path, final JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return extractInteger(node.get(path));
    }

    public static boolean extractBoolean(final JsonNode node) {
        if (node == null || node.isNull()) {
            return false;
        }
        return node.asBoolean();
    }

    public static boolean extractBoolean(final String path, final JsonNode node) {
        if (node == null || node.isNull()) {
            return false;
        }
        return extractBoolean(node.get(path));
    }

    public static LocalDateTime extractLocalDateTime(final String patthern, final JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return LocalDateTime.parse(node.asText(), DateTimeFormatter.ofPattern(patthern));
    }

    public static LocalDateTime extractLocalDateTime(final String path, final String patthern, final JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return extractLocalDateTime(patthern, node.get(path));
    }

    public static void writeNumberField(final JsonGenerator gen, final String field, final Long value) throws IOException {
        if (value != null) {
            gen.writeNumberField(field, value);
        }
    }

    public static void writeBooleanField(final JsonGenerator gen, final String field, final Boolean value) throws IOException {
        if (value != null) {
            gen.writeBooleanField(field, value);
        }
    }

    public static void writeStringField(final JsonGenerator gen, final String field, final Object value) throws IOException {
        if (value != null) {
            gen.writeStringField(field, value.toString());
        }
    }

    public static void writeDateField(final JsonGenerator gen, final String field, final LocalDate value, final String pattern) throws IOException {
        if (value != null) {
            gen.writeStringField(field, value.format(DateTimeFormatter.ofPattern(pattern)));
        }
    }

    public static void writeDateField(final JsonGenerator gen, final String field, final LocalDateTime value, final String pattern) throws IOException {
        if (value != null) {
            gen.writeStringField(field, value.format(DateTimeFormatter.ofPattern(pattern)));
        }
    }

    public static void writeDateField(final JsonGenerator gen, final String field, final ZonedDateTime value, final String pattern) throws IOException {
        if (value != null) {
            gen.writeStringField(field, value.format(DateTimeFormatter.ofPattern(pattern)));
        }
    }
}
