package io.github.iamanjali1003.datacheck.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * The rules to apply, read from a YAML file.
 *
 * columns:
 *   rotor_speed:
 *     range: { min: 0, max: 22.5 }
 *     notNull: {}
 */
public record Spec(Map<String, Map<String, Map<String, Object>>> columns) {

    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    public static Spec load(Path file) throws IOException {
        Spec spec = MAPPER.readValue(file.toFile(), Spec.class);
        if (spec.columns() == null || spec.columns().isEmpty()) {
            throw new IOException("spec has no columns: " + file);
        }
        return spec;
    }
}
