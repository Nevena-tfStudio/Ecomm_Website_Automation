package com.nevena.ecommerce.support;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import org.openqa.selenium.WebDriver;

/** Runs axe-core on the page the browser is showing and reports what it found. */
public final class Accessibility {
    /** WCAG 2.0 and 2.1/2.2 levels A and AA: the usual "conformance" target. */
    private static final List<String> WCAG_A_AA_TAGS =
            List.of("wcag2a", "wcag2aa", "wcag21a", "wcag21aa", "wcag22aa");

    private static final ObjectMapper JSON = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private Accessibility() {
    }

    /**
     * Scans the current page for WCAG A/AA violations and attaches them to the Allure report as JSON.
     * Returns one readable line per violated rule, e.g. "select-name (critical): 1 nodes".
     * The list is empty when axe found nothing.
     */
    public static List<String> findViolations(WebDriver driver, String pageName) {
        return scan(driver, pageName, WCAG_A_AA_TAGS);
    }

    /**
     * Same as {@link #findViolations} but for axe's "best-practice" rules (e.g. a page needs an h1, content
     * belongs inside landmarks). These are good practice rather than WCAG failures, so they are reported apart.
     */
    public static List<String> findBestPracticeViolations(WebDriver driver, String pageName) {
        return scan(driver, pageName, List.of("best-practice"));
    }

    private static List<String> scan(WebDriver driver, String pageName, List<String> tags) {
        Results results = new AxeBuilder().withTags(tags).analyze(driver);
        if (results.isErrored()) { // A scan that did not run must not look like "no violations".
            throw new IllegalStateException("axe could not scan " + pageName + ": " + results.getErrorMessage());
        }
        List<Rule> violations = results.getViolations();
        Allure.attachment("axe violations (" + tags + "): " + pageName, "application/json",
                toJson(violations), AttachmentOptions.withFileExtension("json"));
        return violations.stream().map(Accessibility::summarize).toList();
    }

    /** "rule-id (impact): N nodes". */
    private static String summarize(Rule rule) {
        return rule.getId() + " (" + rule.getImpact() + "): " + rule.getNodes().size() + " nodes";
    }

    /** Short JSON: what each rule says and which elements break it (not axe's whole raw result). */
    private static String toJson(List<Rule> violations) {
        List<Map<String, Object>> rules = violations.stream().map(rule -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id", rule.getId());
            entry.put("impact", rule.getImpact());
            entry.put("help", rule.getHelp());
            entry.put("helpUrl", rule.getHelpUrl());
            entry.put("nodes", rule.getNodes().stream().map(node -> Map.of(
                    "target", String.valueOf(node.getTarget()),
                    "html", String.valueOf(node.getHtml()))).toList());
            return entry;
        }).toList();
        try {
            return JSON.writeValueAsString(rules);
        } catch (Exception e) {
            return "Could not write violations as JSON: " + e;
        }
    }
}
