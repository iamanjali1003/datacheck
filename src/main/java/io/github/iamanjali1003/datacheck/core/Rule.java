package io.github.iamanjali1003.datacheck.core;

import java.util.Map;

/**
 * A single check that can be applied to a column.
 *
 * Implementations are discovered at runtime through {@link java.util.ServiceLoader},
 * so adding a rule means adding a class and listing it in
 * META-INF/services/io.github.iamanjali1003.datacheck.core.Rule - no existing
 * file needs to change.
 */
public interface Rule {

    /** The name used to refer to this rule in a spec file. */
    String name();

    RuleResult check(Dataset data, String column, Map<String, Object> config);
}
