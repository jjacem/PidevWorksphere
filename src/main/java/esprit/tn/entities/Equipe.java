package esprit.tn.entities;

import java.util.List;

public class Equipe {

    private int id;
    private String nomEquipe;
    private List<User> employes;
    private String imageEquipe;



    public Equipe() {}

    public Equipe(int id, String nomEquipe, List<User> employes) {
        this.id = id;
        this.nomEquipe = nomEquipe;
        this.employes = employes;
    }

    public Equipe(int id, String nomEquipe, List<User> employes, String imageEquipe) {
        this.id = id;
        this.nomEquipe = nomEquipe;
        this.employes = employes;
        this.imageEquipe = imageEquipe;
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

    public String getImageEquipe() {
        return imageEquipe;
    }

    public void setImageEquipe(String imageEquipe) {
        this.imageEquipe = imageEquipe;
    }

    @Override
    public String toString() {
        return "Equipe {\n" +
                "  ID        : " + id + "\n" +
                "  Image     : " + imageEquipe + "\n" +
                "  Nom       : " + nomEquipe + "\n" +
                "  Employés  : " + employes + "\n" +
                "}";
    }
}

