package io.github.iamanjali1003.datacheck.report;

import io.github.iamanjali1003.datacheck.core.Report;

import java.io.IOException;

/**
 * Turns a {@link Report} into one output format.
 *
 * Like rules, reporters are found through {@link java.util.ServiceLoader}, so a
 * new format never touches the engine.
 */
public interface Reporter {

    /** The value passed to --report, for example "text" or "json". */
    String format();

    void write(Report report, Appendable out) throws IOException;
}
