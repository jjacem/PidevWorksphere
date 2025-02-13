package esprit.tn.entities;

public class Manager extends User{
    int id_manager;
    int nombreprojet;
    double budget;
    String dapartement_manage;
    public Manager(String nom, String prenom, String email, String mdp, Role role, String adresse, Sexe sexe, int nombreprojet, double budget, String dapartement_manage) {
        super(nom, prenom, email, mdp, role, adresse, sexe);
        this.nombreprojet = nombreprojet;
        this.budget = budget;
        this.dapartement_manage = dapartement_manage;


    }
    public Manager(User u, int nombreprojet, double budget, String dapartement_manage) {
        super(u.getNom(), u.getPrenom(), u.getEmail(), u.getMdp(), u.getRole(), u.getAdresse(), u.getSexe());

        this.nombreprojet = nombreprojet;
        this.budget = budget;
        this.dapartement_manage = dapartement_manage;
    }

    public int getId_manager() {
        return id_manager;
    }

    public void setId_manager(int id_manager) {
        this.id_manager = id_manager;
    }

    public int getNombreprojet() {
        return nombreprojet;
    }

    public void setNombreprojet(int nombreprojet) {
        this.nombreprojet = nombreprojet;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getDapartement_manage() {
        return dapartement_manage;
    }

    public void setDapartement_manage(String dapartement_manage) {
        this.dapartement_manage = dapartement_manage;
    }

    @Override
    public String toString() {
        return super.toString()+"Manager{" +
                "id_manager=" + id_manager +
                ", nombreprojet=" + nombreprojet +
                ", budget=" + budget +
                ", dapartement_manage='" + dapartement_manage + '\'' +
                '}';
    }
}
