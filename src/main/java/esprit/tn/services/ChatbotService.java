package esprit.tn.services;

import esprit.tn.entities.*;

import java.util.List;
import java.util.stream.Collectors;

public class ChatbotService {

    private ServiceEquipe serviceEquipe;

    public ChatbotService() {
        this.serviceEquipe = new ServiceEquipe();
    }

    public String repondre(String question) {
        try {
            List<Equipe> equipes = serviceEquipe.afficherEquipe();

            if (question.toLowerCase().contains("nombre total d'équipes")) {
                return "Le nombre total d'équipes est : " + equipes.size();
            } else if (question.toLowerCase().contains("nombre total de membres")) {
                int totalMembres = equipes.stream()
                        .mapToInt(equipe -> equipe.getEmployes() != null ? equipe.getEmployes().size() : 0)
                        .sum();
                return "Le nombre total de membres est : " + totalMembres;
            } else if (question.toLowerCase().contains("nombre moyen de membres par équipe")) {
                double moyenneMembres = equipes.stream()
                        .mapToInt(equipe -> equipe.getEmployes() != null ? equipe.getEmployes().size() : 0)
                        .average()
                        .orElse(0);
                return "Le nombre moyen de membres par équipe est : " + String.format("%.2f", moyenneMembres);
            } else if (question.toLowerCase().contains("équipe avec le plus de membres")) {
                Equipe equipeAvecPlusDeMembres = equipes.stream()
                        .max((e1, e2) -> Integer.compare(
                                e1.getEmployes() != null ? e1.getEmployes().size() : 0,
                                e2.getEmployes() != null ? e2.getEmployes().size() : 0))
                        .orElse(null);
                return equipeAvecPlusDeMembres != null
                        ? "L'équipe avec le plus de membres est : " + equipeAvecPlusDeMembres.getNomEquipe()
                        : "Aucune équipe trouvée.";
            } else if (question.toLowerCase().contains("équipe avec le moins de membres")) {
                Equipe equipeAvecMoinsDeMembres = equipes.stream()
                        .min((e1, e2) -> Integer.compare(
                                e1.getEmployes() != null ? e1.getEmployes().size() : 0,
                                e2.getEmployes() != null ? e2.getEmployes().size() : 0))
                        .orElse(null);
                return equipeAvecMoinsDeMembres != null
                        ? "L'équipe avec le moins de membres est : " + equipeAvecMoinsDeMembres.getNomEquipe()
                        : "Aucune équipe trouvée.";
            } else if (question.toLowerCase().contains("nombre de projets par équipe")) {
                StringBuilder projetsParEquipe = new StringBuilder("Nombre de projets par équipe :\n");
                equipes.forEach(equipe -> projetsParEquipe.append(equipe.getNomEquipe())
                        .append(" : ")
                        .append(equipe.getNbrProjet())
                        .append(" projets\n"));
                return projetsParEquipe.toString();
            } else {
                return "Je ne comprends pas la question. Posez-moi une question !";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Une erreur s'est produite lors de la récupération des données.";
        }
    }
}
