package tn.esprit.entities;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String mdp;
    private Role role;
    private String adresse;
    private String sexe; // Enum : Femme, Homme
    private String imageProfil;
    private String status; // Enum : Candidature, Entretien, programmé, Embauché, Refusé
    private Double salaireAttendu;
    private String poste;
    private Double salaire;
    private Integer experienceTravail;
    private String departement;
    private String competence;
    private Integer nombreProjet;
    private Double budget;
    private String departementGere;
    private Integer ansExperience;
    private String specialisation;

    public User() {
    }

    public User(int id, String nom, String prenom, String email, String mdp, Role role, String adresse, String sexe, String imageProfil, String status, Double salaireAttendu, String poste, Double salaire, Integer experienceTravail, String departement, String competence, Integer nombreProjet, Double budget, String departementGere, Integer ansExperience, String specialisation) {
        this.id = id;
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

    public User(int id, String nom, String prenom) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
    }

    public User(int id, String nom, String prenom,Role role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
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

    public Double getSalaireAttendu() {
        return salaireAttendu;
    }

    public void setSalaireAttendu(Double salaireAttendu) {
        this.salaireAttendu = salaireAttendu;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public Double getSalaire() {
        return salaire;
    }

    public void setSalaire(Double salaire) {
        this.salaire = salaire;
    }

    public Integer getExperienceTravail() {
        return experienceTravail;
    }

    public void setExperienceTravail(Integer experienceTravail) {
        this.experienceTravail = experienceTravail;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
    }

    public Integer getNombreProjet() {
        return nombreProjet;
    }

    public void setNombreProjet(Integer nombreProjet) {
        this.nombreProjet = nombreProjet;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public String getDepartementGere() {
        return departementGere;
    }

    public void setDepartementGere(String departementGere) {
        this.departementGere = departementGere;
    }

    public Integer getAnsExperience() {
        return ansExperience;
    }

    public void setAnsExperience(Integer ansExperience) {
        this.ansExperience = ansExperience;
    }

    public String getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(String specialisation) {
        this.specialisation = specialisation;
    }

    /*@Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", sexe='" + sexe + '\'' +
                ", poste='" + poste + '\'' +
                ", departement='" + departement + '\'' +
                ", nombreProjet=" + nombreProjet +
                ", budget=" + budget +
                '}';
    }*/


    @Override
    public String toString() {

        return "Employee{" + "id=" + id + ", nom='" + nom + "', prenom='" + prenom + "'}";
    }
}
