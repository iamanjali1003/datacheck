package io.github.iamanjali1003.datacheck.rules;

import io.github.iamanjali1003.datacheck.core.Dataset;
import io.github.iamanjali1003.datacheck.core.RuleResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RangeRuleTest {

    private final RangeRule rule = new RangeRule();

    private static Dataset with(String... values) {
        return new Dataset(Map.of("speed", List.of(values)));
    }

    @Test
    void acceptsValuesInsideTheRange() {
        RuleResult result = rule.check(with("0", "10.5", "22.5"), "speed",
                Map.of("min", 0, "max", 22.5));

        assertThat(result.ok()).isTrue();
    }

    @Test
    void reportsValuesAboveTheMaximum() {
        RuleResult result = rule.check(with("10", "30"), "speed",
                Map.of("min", 0, "max", 22.5));

        assertThat(result.failures()).hasSize(1);
        assertThat(result.failures().get(0)).contains("row 2");
    }

    @Test
    void reportsValuesThatAreNotNumbers() {
        RuleResult result = rule.check(with("10", "fast"), "speed",
                Map.of("min", 0, "max", 22.5));

        assertThat(result.failures()).hasSize(1);
        assertThat(result.failures().get(0)).contains("not a number");
    }

    @Test
    void skipsEmptyCellsSoNotNullCanOwnThem() {
        RuleResult result = rule.check(with("10", ""), "speed",
                Map.of("min", 0, "max", 22.5));

        assertThat(result.ok()).isTrue();
    }
}
