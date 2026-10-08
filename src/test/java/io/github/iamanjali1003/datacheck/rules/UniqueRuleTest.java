package io.github.iamanjali1003.datacheck.rules;

import io.github.iamanjali1003.datacheck.core.Dataset;
import io.github.iamanjali1003.datacheck.core.RuleResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UniqueRuleTest {

    private final UniqueRule rule = new UniqueRule();

    private static Dataset with(String... values) {
        return new Dataset(Map.of("id", List.of(values)));
    }

    @Test
    void passesWhenEveryValueIsDistinct() {
        assertThat(rule.check(with("1", "2", "3"), "id", Map.of()).ok()).isTrue();
    }

    @Test
    void pointsBackAtTheRowWhereTheValueFirstAppeared() {
        RuleResult result = rule.check(with("1", "2", "1"), "id", Map.of());

        assertThat(result.failures()).hasSize(1);
        assertThat(result.failures().get(0)).contains("row 3").contains("row 1");
    }

    @Test
    void ignoresEmptyCells() {
        assertThat(rule.check(with("", "", "1"), "id", Map.of()).ok()).isTrue();
    }
}
