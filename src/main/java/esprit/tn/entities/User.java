package esprit.tn.entities;


import java.util.ArrayList;

public class User {
    private int idUser;
    private String nom, prenom, email, mdp, adresse, imageProfil, poste, departement, competence, departementGere, specialisation;
    private Role role;
    private Sexe sexe;
    private Status status;
    private Double salaireAttendu, salaire, budget;
    private int experienceTravail, nombreProjet, ansExperience;
    ArrayList<Reclamation> reclamations = new ArrayList<Reclamation>();
    ArrayList<Reponse> reponses = new ArrayList<Reponse>();
    private int num;


    public User testManager(String email) {

        return this.Manager("test", "test", email, "test", "test", Sexe.FEMME, "test", "test", 1, 1.0);

    }

    public User testEmploye(String email) {

        return this.Employe("test", "test", email, "test", "test", Sexe.HOMME, "test", "test", 1.0, 1, "test", "test");

    }

    public User testRH(String email) {

        return this.RH("test", "test", email, "test", "test", Sexe.HOMME, "test", 1, "test");

    }

    public User testCandidat(String email) {

        return this.Candidat("test", "test", email, "test", "test", Sexe.HOMME, "test", 1.0);
    }


    public User(String nom, Role role, String prenom, String email, String mdp, String adresse, Sexe sexe, String imageProfil, Status status, Double salaireAttendu, String poste, Double salaire, int experienceTravail, String departement, String competence, int nombreProjet, Double budget, String departementGere, int ansExperience, String specialisation) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.adresse = adresse;
        this.sexe = sexe;
        this.role = role;
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
        this.imageProfil = imageProfil;


    }

    public User() {
    }

    public User(String nom, String prenom, String email, String mdp, String adresse, Sexe sexe, String imageProfil, Double salaireAttendu) {
        {
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
            this.mdp = mdp;
            this.adresse = adresse;
            this.sexe = sexe;
            this.imageProfil = imageProfil;
            this.role = Role.CANDIDAT;
            this.status = Status.Candidature;
            this.salaireAttendu = salaireAttendu;
        }
    }

    public User Candidat(String nom, String prenom, String email, String mdp, String adresse, Sexe sexe, String imageProfil, Double salaireAttendu) {
        User u = new User(nom, prenom, email, mdp, adresse, sexe, imageProfil, salaireAttendu);
        u.setRole(Role.CANDIDAT);
        return u;
    }

    public User(String nom, String prenom, String email, String mdp, String adresse, Sexe sexe, String imageProfil,
                String poste, Double salaire, int experienceTravail, String departement, String competence) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.adresse = adresse;
        this.sexe = sexe;
        this.imageProfil = imageProfil;
        this.poste = poste;
        this.salaire = salaire;
        this.experienceTravail = experienceTravail;
        this.departement = departement;
        this.competence = competence;
        this.role = Role.EMPLOYE;
    }

    public User Employe(String nom, String prenom, String email, String mdp, String adresse, Sexe sexe, String imageProfil
            , String poste, Double salaire, int experienceTravail, String departement, String competence) {
        User u = new User(nom, prenom, email, mdp, adresse, sexe, imageProfil
                , poste, salaire, experienceTravail, departement, competence);
        return u;
    }

    public User(String nom, String prenom, String email, String mdp, String adresse, Sexe sexe, String imageProfil,
                int ansExperience, String specialisation) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.adresse = adresse;
        this.sexe = sexe;
        this.imageProfil = imageProfil;

        this.ansExperience = ansExperience;
        this.specialisation = specialisation;
        this.role = Role.RH;
    }

    public User RH(String nom, String prenom, String email, String mdp, String adresse, Sexe sexe, String imageProfil, int anneeExperience, String specialisation) {
        User u = new User(nom, prenom, email, mdp, adresse, sexe, imageProfil, anneeExperience, specialisation);
        return u;
    }

    public User(String nom, String prenom, String email, String mdp, String adresse, Sexe sexe, String imageProfil,
                String departementGere, int nombreProjet, Double budget) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = Role.MANAGER;
        this.mdp = mdp;
        this.adresse = adresse;
        this.sexe = sexe;
        this.imageProfil = imageProfil;
        this.departementGere = departementGere;
        this.nombreProjet = nombreProjet;
        this.budget = budget;
    }


    public User Manager(String nom, String prenom, String email, String mdp, String adresse, Sexe sexe, String imageProfil, String departementGere, int nombreProjet, Double budget) {
        User u = new User(nom, prenom, email, mdp, adresse, sexe, imageProfil, departementGere, nombreProjet, budget);
        return u;
    }

    public User(int id, String nom, String prenom, Role role) {
        this.idUser = id;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
    }

    public User(int id, String nom, String prenom, Role role, String imageProfil, String email) {
        this.idUser = id;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.imageProfil = imageProfil;
        this.email = email;
    }

    public User(int id, String nom, String prenom, String imageProfil, String email) {
        this.idUser = id;
        this.nom = nom;
        this.prenom = prenom;
        this.imageProfil = imageProfil;
        this.email = email;
    }

    public User(int id, String nom, String prenom, Role role, String imageProfil) {
        this.idUser = id;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.imageProfil = imageProfil;
    }

    public User(int id, String nom, String prenom, String imageProfil) {
        this.idUser = id;
        this.nom = nom;
        this.prenom = prenom;
        this.imageProfil = imageProfil;
    }


    public User(int id, String nom, String prenom) {
        this.idUser = id;
        this.nom = nom;
        this.prenom = prenom;

    }

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

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }

    public String getImageProfil() {
        return imageProfil;
    }

    public void setImageProfil(String imageProfil) {
        this.imageProfil = imageProfil;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
    }

    public int getNombreProjet() {
        return nombreProjet;
    }

    public void setNombreProjet(int nombreProjet) {
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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", adresse='" + adresse + '\'' +
                ", sexe=" + sexe +
                ", imageProfil='" + imageProfil + '\'' +
                ", status=" + status +
                ", salaireAttendu=" + salaireAttendu +
                ", poste='" + poste + '\'' +
                ", salaire=" + salaire +
                ", experienceTravail=" + experienceTravail +
                ", departement='" + departement + '\'' +
                ", competence='" + competence + '\'' +
                ", nombreProjet=" + nombreProjet +
                ", budget=" + budget +
                ", departementGere='" + departementGere + '\'' +
                ", ansExperience=" + ansExperience +
                ", specialisation='" + specialisation + '\'' +
                '}';

    }
}