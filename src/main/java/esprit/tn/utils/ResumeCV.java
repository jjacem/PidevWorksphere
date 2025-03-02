package esprit.tn.utils;

import java.util.Arrays;
import java.util.List;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.ChatCompletions;
import com.azure.ai.inference.models.ChatCompletionsOptions;
import com.azure.ai.inference.models.ChatRequestMessage;
import com.azure.ai.inference.models.ChatRequestSystemMessage;
import com.azure.ai.inference.models.ChatRequestUserMessage;
import com.azure.core.credential.AzureKeyCredential;
import esprit.tn.entities.OffreEmploi;

public class ResumeCV {
    public static String comparer(OffreEmploi offre, String cv) {
        String key = "";
        String endpoint = "https://models.github.ai/inference";
        String model = "gpt-4o";

        ChatCompletionsClient client = new ChatCompletionsClientBuilder()
                .credential(new AzureKeyCredential(key))
                .endpoint(endpoint)
                .buildClient();

        List<ChatRequestMessage> chatMessages = Arrays.asList(
                new ChatRequestSystemMessage("You are a recruiter tasked with evaluating CVs to determine their suitability for a specific job offer. Assess the CVs based on qualifications, skills, experiences, and alignment with the job requirements. Provide a clear and structured assessment, highlighting strengths, weaknesses, and overall fit for the position."),
                new ChatRequestUserMessage("retourner une resume d'une comparaison entre un CV et une offre: " +
                        "this is the cv: " + cv +
                        "this is the offer: " + offre
                )
        );

        ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(chatMessages);
        chatCompletionsOptions.setModel(model);

        ChatCompletions completions = client.complete(chatCompletionsOptions);
        System.out.printf("%s.%n", completions.getChoices().get(0).getMessage().getContent());
        return String.format("%s.%n",completions.getChoices().get(0).getMessage().getContent());
    }
}
