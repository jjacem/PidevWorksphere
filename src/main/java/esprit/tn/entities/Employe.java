package esprit.tn.entities;

public class Employe extends User{
    int id_employe;
    String poste;
    double salaire;
    int experience_travail;
    String departement;
    public Employe(String nom, String prenom, String email, String mdp, Role role, String adresse, Sexe sexe, String poste, double salaire, int experience_travail, String departement) {
        super(nom, prenom, email, mdp, role, adresse, sexe);

        this.poste = poste;
        this.salaire = salaire;
        this.experience_travail = experience_travail;
        this.departement = departement;
    }
    public Employe(User u, String poste, double salaire, int experience_travail, String departement) {
        super(u.getNom(), u.getPrenom(), u.getEmail(), u.getMdp(), u.getRole(), u.getAdresse(), u.getSexe());

        this.poste = poste;
        this.salaire = salaire;
        this.experience_travail = experience_travail;
        this.departement = departement;
    }

    public int getId_employe() {
        return id_employe;
    }

    public void setId_employe(int id_employe) {
        this.id_employe = id_employe;
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

    public int getExperience_travail() {
        return experience_travail;
    }

    public void setExperience_travail(int experience_travail) {
        this.experience_travail = experience_travail;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    @Override
    public String toString() {
        return super.toString()+
                "Employe{" +
                "id_employe=" + id_employe +
                ", poste='" + poste + '\'' +
                ", salaire=" + salaire +
                ", experience_travail=" + experience_travail +
                ", departement='" + departement + '\'' +
                '}';
    }
}
