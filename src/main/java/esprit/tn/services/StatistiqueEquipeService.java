package esprit.tn.services;

import esprit.tn.entities.Equipe;
import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatistiqueEquipeService {

    private ServiceEquipe serviceEquipe;

    public StatistiqueEquipeService() {
        this.serviceEquipe = new ServiceEquipe();
    }

    // Méthode pour obtenir les données des équipes au format JSON
    public String getEquipesDataAsJson() {
        List<Equipe> equipes;
        try {
            equipes = serviceEquipe.afficherEquipe();
        } catch (SQLException e) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", "Une erreur s'est produite lors de la récupération des données.");
            Gson gson = new Gson();
            return gson.toJson(errorData);
        }

        if (equipes == null || equipes.isEmpty()) {
            Map<String, Object> emptyData = new HashMap<>();
            emptyData.put("message", "Aucune équipe trouvée.");
            Gson gson = new Gson();
            return gson.toJson(emptyData);
        }

        Map<String, Object> data = new HashMap<>();

        // Nombre de projets par équipe
        Map<String, Integer> projetsParEquipe = new HashMap<>();
        // Nombre d'employés par équipe
        Map<String, Integer> employesParEquipe = new HashMap<>();

        for (Equipe equipe : equipes) {
            projetsParEquipe.put(equipe.getNomEquipe(), equipe.getNbrProjet());
            employesParEquipe.put(equipe.getNomEquipe(), equipe.getEmployes() != null ? equipe.getEmployes().size() : 0);
        }

        data.put("projetsParEquipe", projetsParEquipe);
        data.put("employesParEquipe", employesParEquipe);

        Gson gson = new Gson();
        return gson.toJson(data);
    }
}