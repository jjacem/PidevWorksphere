package entities;

import java.util.Date;


public class Projet {
    private int id;
    private String nom;
    private String description;
    private Date datecréation, deadline;
    private Equipe equipe;

    public Projet() {
    }

    public Projet(int id, String nom, String description, Date datecréation, Date deadline, Equipe equipe) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.datecréation = datecréation;
        this.deadline = deadline;
        this.equipe = equipe;
    }

    public Projet(String nom, String description, Date datecréation, Date deadline, Equipe equipe) {
        this.nom = nom;
        this.description = description;
        this.datecréation = datecréation;
        this.deadline = deadline;
        this.equipe = equipe;
    }

    public Projet(int id, String nom, String description, Equipe equipe) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.equipe = equipe;
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

    @Override
    public String toString() {
        return "Projet{\n" +
                "ID             :" + id +"\n" +
                "NOM            :" + nom + "\n" +
                "DATE CREATION  :" + datecréation +"\n" +
                "DESCRIPTION    :" + description + "\n" +
                "DEADLINE       :" + deadline  + "\n"+
                "EQUIPE         :" + equipe.getNomEquipe() +"\n"+
                '}';
    }

}
