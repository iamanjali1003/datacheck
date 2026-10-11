package io.github.iamanjali1003.datacheck.core;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EngineTest {

    private final Engine engine = new Engine();

    private static Dataset data() {
        return new Dataset(Map.of("speed", List.of("5", "30")));
    }

    @Test
    void loadsRulesThroughServiceLoader() {
        assertThat(engine.availableRules())
                .contains("range", "notNull", "unique", "matches", "monotonic");
    }

    @Test
    void appliesTheRulesNamedInTheSpec() {
        Spec spec = new Spec(Map.of("speed", Map.of("range", Map.of("min", 0, "max", 22.5))));

        Report report = engine.run(data(), spec, "test");

        assertThat(report.ok()).isFalse();
        assertThat(report.failureCount()).isEqualTo(1);
    }

    @Test
    void reportsAColumnThatIsMissingFromTheData() {
        Spec spec = new Spec(Map.of("missing", Map.of("notNull", Map.of())));

        Report report = engine.run(data(), spec, "test");

        assertThat(report.results().get(0).failures())
                .containsExactly("column is missing from the data");
    }

    @Test
    void reportsARuleNameItDoesNotKnow() {
        Spec spec = new Spec(Map.of("speed", Map.of("nonsense", Map.of())));

        Report report = engine.run(data(), spec, "test");

        assertThat(report.results().get(0).failures().get(0)).contains("unknown rule");
    }
}
