package esprit.tn.services;

import esprit.tn.entities.OffreEmploi;
import esprit.tn.utils.ResumeCV;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CvEvaluationService {
    private static final Logger logger = LoggerFactory.getLogger(CvEvaluationService.class);
    
    public CompletableFuture<String> evaluateCV(OffreEmploi offre, String cvContent) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Starting CV evaluation for job: {}", offre.getTitre());
                String result = ResumeCV.comparer(offre, cvContent);
                logger.info("CV evaluation completed successfully");
                return result;
            } catch (Exception e) {
                logger.error("CV evaluation failed", e);
                throw new RuntimeException("Failed to evaluate CV: " + e.getMessage(), e);
            }
        });
    }
}
