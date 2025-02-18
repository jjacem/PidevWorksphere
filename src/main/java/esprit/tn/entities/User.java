package esprit.tn.entities;

import java.util.List;

public class User {

    private int idUser;
    private String nom;
    private String prenom;
    private String email;
    private String mdp;
    private Role role;
    private String adresse;
    private String sexe;
    private String imageProfil;
    private String status;
    private double salaireAttendu;
    private String poste;
    private double salaire;
    private int experienceTravail;
    private String departement;
    private List<String> competence;
    private int nombreProjet;
    private double budget;
    private String departementGere;
    private int ansExperience;
    private String specialisation;

    // Constructeur

    public User(String nom, String prenom, String email, String mdp, Role role, String adresse, String sexe, String imageProfil, String status, double salaireAttendu, String poste, double salaire, int experienceTravail, String departement, List<String> competence, int nombreProjet, double budget, String departementGere, int ansExperience, String specialisation) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.role = role;
        this.adresse = adresse;
        this.sexe = sexe;
        this.imageProfil = imageProfil;
        this.status = status;
        this.salaireAttendu = salaireAttendu;
        this.poste = poste;
        this.salaire = salaire;
        this.experienceTravail = experienceTravail;
        this.departement = departement;
        this.competence = competence;
        this.nombreProjet = nombreProjet;
        this.budget = budget;
        this.departementGere = departementGere;
        this.ansExperience = ansExperience;
        this.specialisation = specialisation;
    }

    public User(int idUser, String nom, String prenom) {
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
    }

    // Getters et Setters
    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getImageProfil() {
        return imageProfil;
    }

    public void setImageProfil(String imageProfil) {
        this.imageProfil = imageProfil;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getSalaireAttendu() {
        return salaireAttendu;
    }

    public void setSalaireAttendu(double salaireAttendu) {
        this.salaireAttendu = salaireAttendu;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public double getSalaire() {
        return salaire;
    }

    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    public int getExperienceTravail() {
        return experienceTravail;
    }

    public void setExperienceTravail(int experienceTravail) {
        this.experienceTravail = experienceTravail;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public List<String> getCompetence() {
        return competence;
    }

    public void setCompetence(List<String> competence) {
        this.competence = competence;
    }

    public int getNombreProjet() {
        return nombreProjet;
    }

    public void setNombreProjet(int nombreProjet) {
        this.nombreProjet = nombreProjet;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getDepartementGere() {
        return departementGere;
    }

    public void setDepartementGere(String departementGere) {
        this.departementGere = departementGere;
    }

    public int getAnsExperience() {
        return ansExperience;
    }

    public void setAnsExperience(int ansExperience) {
        this.ansExperience = ansExperience;
    }

    public String getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(String specialisation) {
        this.specialisation = specialisation;
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", mdp='" + mdp + '\'' +
                ", role=" + role +
                ", adresse='" + adresse + '\'' +
                ", sexe='" + sexe + '\'' +
                ", imageProfil='" + imageProfil + '\'' +
                ", status='" + status + '\'' +
                ", salaireAttendu=" + salaireAttendu +
                ", poste='" + poste + '\'' +
                ", salaire=" + salaire +
                ", experienceTravail=" + experienceTravail +
                ", departement='" + departement + '\'' +
                ", competence=" + competence +
                ", nombreProjet=" + nombreProjet +
                ", budget=" + budget +
                ", departementGere='" + departementGere + '\'' +
                ", ansExperience=" + ansExperience +
                ", specialisation='" + specialisation + '\'' +
                '}';
    }
}
