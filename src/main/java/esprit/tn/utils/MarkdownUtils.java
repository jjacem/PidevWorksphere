package esprit.tn.utils;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;

import java.util.Arrays;
import java.util.List;

public class MarkdownUtils {
    
    /**
     * Converts Markdown text to HTML
     */
    public static String markdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        
        List<org.commonmark.Extension> extensions = Arrays.asList(
            TablesExtension.create(),
            StrikethroughExtension.create()
        );
        
        Parser parser = Parser.builder()
            .extensions(extensions)
            .build();
            
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder()
            .extensions(extensions)
            .build();
            
        return renderer.render(document);
    }
    
    /**
     * Formats the CV evaluation result as Markdown
     */
    public static String formatEvaluationAsMarkdown(String evaluationText) {
        // If the evaluation is already in markdown format, return as is
        if (evaluationText.contains("#") || evaluationText.contains("**")) {
            return evaluationText;
        }
        
        // Simple formatting to convert plain text to markdown
        StringBuilder markdown = new StringBuilder();
        markdown.append("# CV Evaluation Results\n\n");
        
        // Look for sections and format them
        String[] lines = evaluationText.split("\n");
        boolean inList = false;
        
        for (String line : lines) {
            line = line.trim();
            
            if (line.isEmpty()) {
                markdown.append("\n");
                inList = false;
                continue;
            }
            
            // Handle percentage matches
            if (line.toLowerCase().contains("match") && line.contains("%")) {
                markdown.append("## ").append(line).append("\n\n");
                continue;
            }
            
            // Handle section headers
            if (line.endsWith(":") && line.length() < 50) {
                markdown.append("## ").append(line).append("\n\n");
                continue;
            }
            
            // Handle list items
            if (line.startsWith("-") || line.startsWith("•") || 
                line.matches("\\d+\\..*")) {
                if (!inList) {
                    markdown.append("\n");
                    inList = true;
                }
                markdown.append(line).append("\n");
                continue;
            }
            
            // Handle strengths, weaknesses, etc.
            if (line.toLowerCase().contains("strength") || 
                line.toLowerCase().contains("weakness") || 
                line.toLowerCase().contains("recommendation") ||
                line.toLowerCase().contains("qualification") ||
                line.toLowerCase().contains("skill")) {
                markdown.append("### ").append(line).append("\n\n");
                continue;
            }
            
            // Normal paragraph
            markdown.append(line).append("\n\n");
        }
        
        return markdown.toString();
    }
}
