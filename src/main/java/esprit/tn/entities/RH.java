package esprit.tn.entities;

public class RH extends User{
int id_rh;
String competence, specialisation;
int and_exeprience;
    public RH(String nom, String prenom, String email, String mdp, Role role, String adresse, Sexe sexe, int id_rh, String competence, String specialisation, int and_exeprience) {
        super(nom, prenom, email, mdp, role, adresse, sexe);
        this.id_rh = id_rh;
        this.competence = competence;
        this.specialisation = specialisation;
        this.and_exeprience = and_exeprience;

    }
    public RH(User u, String competence, String specialisation, int and_exeprience) {
        super(u.getNom(), u.getPrenom(), u.getEmail(), u.getMdp(), u.getRole(), u.getAdresse(), u.getSexe());

        this.id_rh = id_rh;
        this.competence = competence;
        this.specialisation = specialisation;
        this.and_exeprience = and_exeprience;
    }

    public int getId_rh() {
        return id_rh;
    }

    public void setId_rh(int id_rh) {
        this.id_rh = id_rh;
    }

    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
    }

    public String getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(String specialisation) {
        this.specialisation = specialisation;
    }

    public int getAnd_exeprience() {
        return and_exeprience;
    }

    public void setAnd_exeprience(int and_exeprience) {
        this.and_exeprience = and_exeprience;
    }

    @Override
    public String toString() {
        return super.toString()+ "RH{" +
                "id_rh=" + id_rh +
                ", competence='" + competence + '\'' +
                ", specialisation='" + specialisation + '\'' +
                ", and_exeprience=" + and_exeprience +
                '}';
    }
}
