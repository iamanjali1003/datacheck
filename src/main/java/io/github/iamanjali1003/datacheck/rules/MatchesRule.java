package io.github.iamanjali1003.datacheck.rules;

import io.github.iamanjali1003.datacheck.core.Dataset;
import io.github.iamanjali1003.datacheck.core.Rule;
import io.github.iamanjali1003.datacheck.core.RuleResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** Checks that every value matches a regular expression. */
public final class MatchesRule implements Rule {

    @Override
    public String name() {
        return "matches";
    }

    @Override
    public RuleResult check(Dataset data, String column, Map<String, Object> config) {
        Object pattern = config.get("pattern");
        if (pattern == null) {
            return new RuleResult(name(), column, List.of("matches needs a pattern"));
        }

        Pattern compiled;
        try {
            compiled = Pattern.compile(pattern.toString());
        } catch (PatternSyntaxException e) {
            return new RuleResult(name(), column, List.of("invalid pattern: " + e.getDescription()));
        }

        List<String> failures = new ArrayList<>();
        List<String> values = data.column(column);

        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            if (value.isEmpty()) {
                continue;
            }
            if (!compiled.matcher(value).matches()) {
                failures.add("row " + (i + 1) + ": " + value + " does not match " + pattern);
            }
        }
        return new RuleResult(name(), column, failures);
    }
}
