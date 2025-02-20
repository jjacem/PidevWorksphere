package esprit.tn.entities;

import java.util.List;

public class Equipe {

    private int id;
    private String nomEquipe;
    private List<User> employes;



    public Equipe() {}

    public Equipe(int id, String nomEquipe, List<User> employes) {
        this.id = id;
        this.nomEquipe = nomEquipe;
        this.employes = employes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomEquipe() {
        return nomEquipe;
    }

    public void setNomEquipe(String nomEquipe) {
        this.nomEquipe = nomEquipe;
    }

    public List<User> getEmployes() {
        return employes;
    }

    public void setEmployes(List<User> employes) {
        this.employes = employes;
    }

    @Override
    public String toString() {
        return "Equipe {\n" +
                "  ID        : " + id + "\n" +
                "  Nom       : " + nomEquipe + "\n" +
                "  Employés  : " + employes + "\n" +
                "}";
    }
}


