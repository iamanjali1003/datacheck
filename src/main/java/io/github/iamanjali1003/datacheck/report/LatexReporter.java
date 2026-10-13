package io.github.iamanjali1003.datacheck.report;

import io.github.iamanjali1003.datacheck.core.Report;
import io.github.iamanjali1003.datacheck.core.RuleResult;

import java.io.IOException;

/**
 * A LaTeX document, for when the result has to go into an engineering review.
 *
 * Writes the .tex source only. Run it through pdflatex to get a PDF.
 */
public final class LatexReporter implements Reporter {

    @Override
    public String format() {
        return "latex";
    }

    @Override
    public void write(Report report, Appendable out) throws IOException {
        out.append("\\documentclass[11pt,a4paper]{article}\n")
           .append("\\usepackage[margin=2.5cm]{geometry}\n")
           .append("\\usepackage{longtable}\n")
           .append("\\usepackage{booktabs}\n")
           .append("\\begin{document}\n\n")
           .append("\\section*{Validation report}\n\n")
           .append("Source: \\texttt{").append(escape(report.source())).append("}\n\n");

        out.append("\\begin{longtable}{lll}\n")
           .append("\\toprule\n")
           .append("Column & Rule & Result \\\\\n")
           .append("\\midrule\n")
           .append("\\endhead\n");

        for (RuleResult result : report.results()) {
            out.append(escape(result.column())).append(" & ")
               .append(escape(result.rule())).append(" & ")
               .append(result.ok() ? "pass" : result.failures().size() + " failed")
               .append(" \\\\\n");
        }

        out.append("\\bottomrule\n\\end{longtable}\n\n");

        if (!report.ok()) {
            out.append("\\section*{Failures}\n\n");
            for (RuleResult result : report.results()) {
                if (result.ok()) {
                    continue;
                }
                out.append("\\subsection*{")
                   .append(escape(result.column())).append(" / ").append(escape(result.rule()))
                   .append("}\n\\begin{itemize}\n");
                for (String failure : result.failures()) {
                    out.append("  \\item ").append(escape(failure)).append("\n");
                }
                out.append("\\end{itemize}\n\n");
            }
        }

        out.append("\\end{document}\n");
    }

    private static String escape(String text) {
        return text.replace("\\", "\\textbackslash{}")
                   .replace("&", "\\&")
                   .replace("%", "\\%")
                   .replace("$", "\\$")
                   .replace("#", "\\#")
                   .replace("_", "\\_")
                   .replace("{", "\\{")
                   .replace("}", "\\}");
    }
}
