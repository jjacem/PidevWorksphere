package entities;

public class User {

    private int id;
    private String nom ;
    private String prenom ;
    private String mdp ;
    private String email;
    private Role role;

    public User() {}

    public User(String nom, String prenom, String mdp, String email, String password, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.mdp = mdp;
        this.email = email;
        this.role = role;
    }

    public User(int id, String nom, String prenom, String mdp, String email, String password, Role role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.mdp = mdp;
        this.email = email;
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

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", mdp='" + mdp + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }


}
