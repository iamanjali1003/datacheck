package io.github.iamanjali1003.datacheck.rules;

import io.github.iamanjali1003.datacheck.core.Dataset;
import io.github.iamanjali1003.datacheck.core.RuleResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MonotonicRuleTest {

    private final MonotonicRule rule = new MonotonicRule();

    private static Dataset with(String... values) {
        return new Dataset(Map.of("t", List.of(values)));
    }

    @Test
    void increasingIsTheDefault() {
        assertThat(rule.check(with("1", "2", "3"), "t", Map.of()).ok()).isTrue();
    }

    @Test
    void equalValuesAreAllowed() {
        assertThat(rule.check(with("1", "1", "2"), "t", Map.of()).ok()).isTrue();
    }

    @Test
    void reportsAStepBackwards() {
        RuleResult result = rule.check(with("1", "5", "3"), "t", Map.of());

        assertThat(result.failures()).hasSize(1);
        assertThat(result.failures().get(0)).contains("row 3");
    }

    @Test
    void handlesDecreasing() {
        assertThat(rule.check(with("3", "2", "1"), "t",
                Map.of("direction", "decreasing")).ok()).isTrue();
    }

    @Test
    void rejectsAnUnknownDirection() {
        RuleResult result = rule.check(with("1"), "t", Map.of("direction", "sideways"));

        assertThat(result.failures().get(0)).contains("increasing or decreasing");
    }
}
