package io.github.iamanjali1003.datacheck.report;

import io.github.iamanjali1003.datacheck.core.Report;
import io.github.iamanjali1003.datacheck.core.RuleResult;

import java.io.IOException;

/** A single self contained HTML page. */
public final class HtmlReporter implements Reporter {

    @Override
    public String format() {
        return "html";
    }

    @Override
    public void write(Report report, Appendable out) throws IOException {
        out.append("<!doctype html>\n<html lang=\"en\">\n<head>\n")
           .append("<meta charset=\"utf-8\">\n")
           .append("<title>datacheck report</title>\n")
           .append("<style>\n")
           .append("body{font:14px/1.6 system-ui,sans-serif;max-width:50rem;margin:2rem auto;padding:0 1rem}\n")
           .append("table{border-collapse:collapse;width:100%}\n")
           .append("th,td{text-align:left;padding:.4rem .6rem;border-bottom:1px solid #ddd}\n")
           .append("ul{margin:.3rem 0 1rem 1.2rem;color:#555}\n")
           .append(".fail{color:#b00}\n")
           .append("</style>\n</head>\n<body>\n");

        out.append("<h1>Validation report</h1>\n")
           .append("<p>Source: <code>").append(escape(report.source())).append("</code></p>\n");

        out.append("<table>\n<tr><th>Column</th><th>Rule</th><th>Result</th></tr>\n");
        for (RuleResult result : report.results()) {
            out.append("<tr><td>").append(escape(result.column()))
               .append("</td><td>").append(escape(result.rule()))
               .append("</td><td")
               .append(result.ok() ? ">pass" : " class=\"fail\">" + result.failures().size() + " failed")
               .append("</td></tr>\n");
        }
        out.append("</table>\n");

        for (RuleResult result : report.results()) {
            if (result.ok()) {
                continue;
            }
            out.append("<h2>").append(escape(result.column()))
               .append(" / ").append(escape(result.rule())).append("</h2>\n<ul>\n");
            for (String failure : result.failures()) {
                out.append("<li>").append(escape(failure)).append("</li>\n");
            }
            out.append("</ul>\n");
        }

        out.append("</body>\n</html>\n");
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;");
    }
}
