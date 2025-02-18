package esprit.tn.entities;


public class User {
    private int id;
    private String nom, prenom, email, mdp, adresse;
    private Role role;


    public User() {
    }

    public User(int id, String nom, String prenom, Role role, String email, String mdp, String adresse) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.email = email;
        this.mdp = mdp;
        this.adresse = adresse;
    }

    public User(String nom, String prenom, Role role, String email, String mdp, String adresse) {
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.email = email;
        this.mdp = mdp;
        this.adresse = adresse;
    }


    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public Role getRole() {
        return role;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", mdp='" + mdp + '\'' +
                ", adresse='" + adresse + '\'' +
                ", role=" + role +
                '}';
    }
}
