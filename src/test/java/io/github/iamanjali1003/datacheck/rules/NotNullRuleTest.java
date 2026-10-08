package io.github.iamanjali1003.datacheck.rules;

import io.github.iamanjali1003.datacheck.core.Dataset;
import io.github.iamanjali1003.datacheck.core.RuleResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NotNullRuleTest {

    private final NotNullRule rule = new NotNullRule();

    private static Dataset with(String... values) {
        return new Dataset(Map.of("name", List.of(values)));
    }

    @Test
    void passesWhenEveryCellHasAValue() {
        assertThat(rule.check(with("a", "b"), "name", Map.of()).ok()).isTrue();
    }

    @Test
    void reportsTheRowOfEachEmptyCell() {
        RuleResult result = rule.check(with("a", "", "c", ""), "name", Map.of());

        assertThat(result.failures()).containsExactly("row 2: empty", "row 4: empty");
    }
}
