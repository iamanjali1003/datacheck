package io.github.iamanjali1003.datacheck.rules;

import io.github.iamanjali1003.datacheck.core.Dataset;
import io.github.iamanjali1003.datacheck.core.RuleResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MatchesRuleTest {

    private final MatchesRule rule = new MatchesRule();

    private static Dataset with(String... values) {
        return new Dataset(Map.of("code", List.of(values)));
    }

    @Test
    void passesWhenEveryValueMatches() {
        assertThat(rule.check(with("AB-1", "CD-2"), "code",
                Map.of("pattern", "[A-Z]{2}-\\d")).ok()).isTrue();
    }

    @Test
    void reportsValuesThatDoNotMatch() {
        RuleResult result = rule.check(with("AB-1", "nope"), "code",
                Map.of("pattern", "[A-Z]{2}-\\d"));

        assertThat(result.failures()).hasSize(1);
        assertThat(result.failures().get(0)).contains("row 2");
    }

    @Test
    void needsAPattern() {
        RuleResult result = rule.check(with("a"), "code", Map.of());

        assertThat(result.failures()).containsExactly("matches needs a pattern");
    }

    @Test
    void reportsAnInvalidPatternRatherThanThrowing() {
        RuleResult result = rule.check(with("a"), "code", Map.of("pattern", "[unclosed"));

        assertThat(result.failures().get(0)).contains("invalid pattern");
    }
}
