package esprit.tn.entities;

import java.util.List;

public class Equipe {

    private int id;
    private String nomEquipe;
    private List<User> employes;
    private String imageEquipe;
    private int nbrProjet;
    private List<Projet> projets;
    private User user;
    private int id_user;

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

    public Equipe(int id, String nomEquipe, List<User> employes, String imageEquipe,int nbrProjet) {
        this.id = id;
        this.nomEquipe = nomEquipe;
        this.employes = employes;
        this.imageEquipe = imageEquipe;
        this.nbrProjet = nbrProjet;

    }
    public Equipe(int id, String nomEquipe, List<User> employes, String imageEquipe,int nbrProjet, int id_user) {
        this.id = id;
        this.nomEquipe = nomEquipe;
        this.employes = employes;
        this.imageEquipe = imageEquipe;
        this.nbrProjet = nbrProjet;
        this.id_user = id_user;
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

    public int getNbrProjet() {
        return nbrProjet;
    }
    public List<Projet> getProjets() {
        return projets;
    }

    public void setProjets(List<Projet> projets) {
        this.projets = projets;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    @Override
    public String toString() {
        return "Equipe {\n" +
                "  ID        : " + id + "\n" +
                "  Image     : " + imageEquipe + "\n" +
                "  Nom       : " + nomEquipe + "\n" +
                "  Employés  : " + employes + "\n" +
                "  NbrProjet : " + nbrProjet + "\n" +
                "}";
    }
}

