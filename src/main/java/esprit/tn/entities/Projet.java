package esprit.tn.entities;

import java.util.Date;


public class Projet {
    private int id;
    private String nom;
    private String description;
    private Date datecréation, deadline;
    private EtatProjet etat;
    private Equipe equipe;

    public Projet() {
    }

    public Projet(int id, String nom, String description, Date datecréation, Date deadline,  EtatProjet etat,Equipe equipe) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.datecréation = datecréation;
        this.deadline = deadline;
        this.etat = etat;
        this.equipe = equipe;

    }
    public Projet(int id, String nom, String description, Date datecréation, Date deadline,  EtatProjet etat) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.datecréation = datecréation;
        this.deadline = deadline;
        this.etat = etat;


    }



    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDatecréation() {
        return datecréation;
    }

    public void setDatecréation(Date datecréation) {
        this.datecréation = datecréation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public EtatProjet getEtat() {
        return etat;
    }

    public void setEtat(EtatProjet etat) {
        this.etat = etat;
    }


    @Override
    public String toString() {
        return "Projet{\n" +
                "ID             :" + id +"\n" +
                "NOM            :" + nom + "\n" +
                "DATE CREATION  :" + datecréation +"\n" +
                "DESCRIPTION    :" + description + "\n" +
                "DEADLINE       :" + deadline  + "\n"+
                "ETAT           :" + etat + "\n"+
                "EQUIPE         :" + equipe.getNomEquipe() +"\n"+
                '}';
    }

}
