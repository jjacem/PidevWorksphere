package esprit.tn.entities;

import java.util.List;

public class Sponsor {

    private int idSponsor;
    private String nomSponso;
    private String prenomSponso;
    private String emailSponso;
    private double budgetSponso;
    private List<Evenement> evenements; // Liste d'événements soutenus par ce sponsor

    public Sponsor(String nomSponso, String prenomSponso, String emailSponso, double budgetSponso) {
        this.nomSponso = nomSponso;
        this.prenomSponso = prenomSponso;
        this.emailSponso = emailSponso;
        this.budgetSponso = budgetSponso;
    }
   public Sponsor(String nomSponso, String prenomSponso, String emailSponso, double budgetSponso, List<Evenement> evenements) {
       this.nomSponso = nomSponso;
       this.prenomSponso = prenomSponso;
       this.emailSponso = emailSponso;
       this.budgetSponso = budgetSponso;
       this.evenements = evenements;
   }

    // Getters et Setters
    public List<Evenement> getEvenements() {
        return evenements;
    }

    public void setEvenements(List<Evenement> evenements) {
        this.evenements = evenements;
    }
    public int getIdSponsor() {
        return idSponsor;
    }

    public void setIdSponsor(int idSponsor) {
        this.idSponsor = idSponsor;
    }

    public String getNomSponso() {
        return nomSponso;
    }

    public void setNomSponso(String nomSponso) {
        this.nomSponso = nomSponso;
    }

    public String getPrenomSponso() {
        return prenomSponso;
    }

    public void setPrenomSponso(String prenomSponso) {
        this.prenomSponso = prenomSponso;
    }

    public String getEmailSponso() {
        return emailSponso;
    }

    public void setEmailSponso(String emailSponso) {
        this.emailSponso = emailSponso;
    }

    public double getBudgetSponso() {
        return budgetSponso;
    }

    public void setBudgetSponso(double budgetSponso) {
        this.budgetSponso = budgetSponso;
    }
}
