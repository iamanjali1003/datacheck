package io.github.iamanjali1003.datacheck.report;

import io.github.iamanjali1003.datacheck.core.Report;
import io.github.iamanjali1003.datacheck.core.RuleResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LatexReporterTest {

    private final LatexReporter reporter = new LatexReporter();

    private String render(String column, String failure) throws IOException {
        Report report = new Report("data.csv",
                List.of(new RuleResult("range", column, List.of(failure))));
        StringBuilder out = new StringBuilder();
        reporter.write(report, out);
        return out.toString();
    }

    @Test
    void escapesUnderscoresInColumnNames() throws IOException {
        assertThat(render("rotor_speed", "x")).contains("rotor\\_speed");
    }

    @Test
    void escapesABackslashWithoutReprocessingItsOwnBraces() throws IOException {
        String tex = render("path", "C:\\temp");

        assertThat(tex).contains("\\textbackslash{}");
        assertThat(tex).doesNotContain("\\textbackslash\\{\\}");
    }

    @Test
    void escapesTheRemainingSpecialCharacters() throws IOException {
        String tex = render("odd", "100% & $5 #1 {x} ^2 ~y");

        assertThat(tex).contains("100\\% \\& \\$5 \\#1 \\{x\\}");
        assertThat(tex).contains("\\textasciicircum{}2");
        assertThat(tex).contains("\\textasciitilde{}y");
    }

    @Test
    void leavesOrdinaryTextAlone() throws IOException {
        assertThat(render("speed", "row 3: 31.4 outside [0.0, 22.5]"))
                .contains("row 3: 31.4 outside [0.0, 22.5]");
    }
}
