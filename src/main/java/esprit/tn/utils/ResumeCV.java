package esprit.tn.utils;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.*;
import com.azure.core.credential.AzureKeyCredential;
import esprit.tn.entities.OffreEmploi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResumeCV {
    private static final Logger logger = LoggerFactory.getLogger(ResumeCV.class);

    public static String comparer(OffreEmploi offre, String cv) {
        if (offre == null || cv == null || cv.trim().isEmpty()) {
            return "Error: Missing CV or job offer details";
        }

        try {
            String key = "";
            String endpoint = "https://models.inference.ai.azure.com";

            // Clean up and simplify the input data
            String cleanCvContent = sanitizeText(cv);
            String cleanJobTitle = sanitizeText(offre.getTitre());
            String cleanDescription = sanitizeText(offre.getDescription());
            String cleanExperience = sanitizeText(offre.getExperience());

            // Create client
            ChatCompletionsClient client = new ChatCompletionsClientBuilder()
                    .credential(new AzureKeyCredential(key))
                    .endpoint(endpoint)
                    .buildClient();

            // Create content map for structured message
            Map<String, String> contentMap = new HashMap<>();
            contentMap.put("role", "user");

            // Create a simple, single-line content
            String content = "Analyze CV: " + cleanCvContent + " | Job: " + cleanJobTitle +
                    " | Description: " + cleanDescription + " | Experience: " + cleanExperience;

            // Create a basic system prompt
            String systemPrompt = "You are a professional HR recruiter. Evaluate the CV against the job requirements.";

            // Create the messages
            List<ChatRequestMessage> messages = Arrays.asList(
                    new ChatRequestSystemMessage(systemPrompt),
                    new ChatRequestUserMessage(content)
            );

            // Configure the completion options
            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
            options.setModel("gpt-4o");

            // Get completion
            ChatCompletions response = client.complete(options);

            if (response != null && !response.getChoices().isEmpty()) {
                String result = response.getChoices().get(0).getMessage().getContent();
                return formatResult(result);
            }

            return "Unable to generate evaluation.";

        } catch (Exception e) {
            logger.error("Error in CV evaluation: ", e);
            return "Error during evaluation: " + e.getMessage();
        }
    }

    private static String sanitizeText(String text) {
        if (text == null) return "";

        // Replace all whitespace and control characters with a single space
        return text.replaceAll("[\\s\\p{Cntrl}]+", " ").trim();
    }

    private static String formatResult(String result) {
        // Format the result into Markdown sections if it's not already in Markdown
        if (!result.contains("#") && !result.contains("**")) {
            StringBuilder markdown = new StringBuilder();
            markdown.append("# CV Evaluation\n\n");

            String[] parts = result.split("\\.|\\n");
            for (String part : parts) {
                part = part.trim();
                if (part.isEmpty()) continue;

                if (part.toLowerCase().contains("match") ||
                        part.toLowerCase().contains("qualification") ||
                        part.toLowerCase().contains("skill") ||
                        part.toLowerCase().contains("recommendation")) {
                    markdown.append("## ").append(part).append("\n\n");
                } else {
                    markdown.append(part).append(".\n\n");
                }
            }

            return markdown.toString();
        }

        return result;
    }
}