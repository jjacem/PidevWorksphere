package esprit.tn.entities;

import java.util.List;

public class Sponsor {

    private int idSponsor;
    private String nomSponso;
    private String prenomSponso;
    private String emailSponso;
    private double budgetSponso;
    private double BudgetApresReduction;
    private Classement classement = Classement.Bronze;
    private String secteurSponsor; // Nouvel attribut
    private List<Evenement> evenements; // Liste d'événements soutenus par ce sponsor

    public Sponsor() {}

    public Sponsor(String nomSponso, String prenomSponso, String emailSponso, double budgetSponso, String secteurSponsor) {
        this.nomSponso = nomSponso;
        this.prenomSponso = prenomSponso;
        this.emailSponso = emailSponso;
        this.budgetSponso = budgetSponso;
        this.secteurSponsor = secteurSponsor;
    }

    public Sponsor(String nomSponso, String prenomSponso, String emailSponso, double budgetSponso, String secteurSponsor, List<Evenement> evenements) {
        this.nomSponso = nomSponso;
        this.prenomSponso = prenomSponso;
        this.emailSponso = emailSponso;
        this.budgetSponso = budgetSponso;
        this.secteurSponsor = secteurSponsor;
        this.evenements = evenements;
    }

    public Sponsor(String nomSponso) {
        this.nomSponso = nomSponso;
    }

    // Getters et Setters
    public String getSecteurSponsor() {
        return secteurSponsor;
    }

    public void setSecteurSponsor(String secteurSponsor) {
        this.secteurSponsor = secteurSponsor;
    }

    public double getBudgetApresReduction() {
        return BudgetApresReduction;
    }

    public void setBudgetApresReduction(double budgetApresReduction) {
        BudgetApresReduction = budgetApresReduction;
    }

    public List<Evenement> getEvenements() {
        return evenements;
    }

    public Classement getClassement() {
        return classement;
    }

    public void setClassement(Classement classement) {
        this.classement = classement;
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

    @Override
    public String toString() {
        return "Sponsor{" +
                "nomSponso='" + nomSponso + '\'' +
                ", prenomSponso='" + prenomSponso + '\'' +
                ", emailSponso='" + emailSponso + '\'' +
                ", budgetSponso=" + budgetSponso +
                ", secteurSponsor='" + secteurSponsor + '\'' +
                ", evenements=" + evenements +
                '}';
    }
}