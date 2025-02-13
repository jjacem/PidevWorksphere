package esprit.tn.entities;

public class User {
    private int idUser;
    private String nom, prenom, email, mdp, adresse, imageProfil;
    private Role role;
    private Sexe sexe;



    public User() {
    }


    public User(String nom, String prenom, String email, String mdp, String adresse) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.role = Role.CANDIDAT;
        this.adresse = adresse;
        this.sexe = Sexe.HOMME;
        this.imageProfil = imageProfil;
    }

    public User(String nom, String prenom, String email, String mdp, String adresse, Sexe sexe) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.role = Role.CANDIDAT;
        this.adresse = adresse;
        this.sexe = sexe;
        this.imageProfil = imageProfil;
    }


    public User(String nom, String prenom, String email, String mdp, Role role, String adresse, Sexe sexe) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.role = role;
        this.adresse = adresse;
        this.sexe = sexe;
        this.imageProfil = imageProfil;
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

    // toString method
    @Override
    public String toString() {
        return "User{" +

                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", adresse='" + adresse + '\'' +
                ", sexe=" + sexe +
                ", imageProfil='" + imageProfil + '\'' +
                '}';
    }
}

