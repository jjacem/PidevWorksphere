package esprit.tn.services;

import esprit.tn.entities.*;

import java.util.List;

public class ChatbotEquipeService {

    private ServiceEquipe serviceEquipe;

    public ChatbotEquipeService() {
        this.serviceEquipe = new ServiceEquipe();
    }

    public String repondre(String question) {
        try {
            List<Equipe> equipes = serviceEquipe.afficherEquipe();

            if (question.toLowerCase().contains("nombre total d'équipes")) {
                return "Le nombre total d'équipes est : " + equipes.size();
            } else if (question.toLowerCase().contains("nombre total des employés")) {
                int totalEmployes = equipes.stream()
                        .mapToInt(equipe -> equipe.getEmployes() != null ? equipe.getEmployes().size() : 0)
                        .sum();
                return "Le nombre total des employés est : " + totalEmployes;
            } else if (question.toLowerCase().contains("nombre moyen d'employé par équipe")) {
                double moyenneEmployes = equipes.stream()
                        .mapToInt(equipe -> equipe.getEmployes() != null ? equipe.getEmployes().size() : 0)
                        .average()
                        .orElse(0);
                return "Le nombre moyen d'employé par équipe est : " + String.format("%.2f", moyenneEmployes);
            } else if (question.toLowerCase().contains("équipe avec le plus d'employés")) {
                Equipe equipeAvecPlusDEmployes = equipes.stream()
                        .max((e1, e2) -> Integer.compare(
                                e1.getEmployes() != null ? e1.getEmployes().size() : 0,
                                e2.getEmployes() != null ? e2.getEmployes().size() : 0))
                        .orElse(null);
                return equipeAvecPlusDEmployes != null
                        ? "L'équipe avec le plus d'employés est : " + equipeAvecPlusDEmployes.getNomEquipe()
                        : "Aucune équipe trouvée.";
            } else if (question.toLowerCase().contains("équipe avec le moins d'employés")) {
                Equipe equipeAvecMoinsDEmployes = equipes.stream()
                        .min((e1, e2) -> Integer.compare(
                                e1.getEmployes() != null ? e1.getEmployes().size() : 0,
                                e2.getEmployes() != null ? e2.getEmployes().size() : 0))
                        .orElse(null);
                return equipeAvecMoinsDEmployes != null
                        ? "L'équipe avec le moins d'employés est : " + equipeAvecMoinsDEmployes.getNomEquipe()
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