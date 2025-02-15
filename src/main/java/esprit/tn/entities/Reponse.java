package esprit.tn.entities;

public class Reponse {
    int id_reponse;
    String message;
    int id_employe, id_reclamation;

    public int getId_reclamation() {
        return id_reclamation;
    }

    public void setId_reclamation(int id_reclamation) {
        this.id_reclamation = id_reclamation;
    }

    public Reponse(String message, int id_employe, int id_reclamation) {
        super();
        this.message = message;
        this.id_employe = id_employe;
        this.id_reclamation = id_reclamation;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId_employe() {
        return id_employe;
    }

    public void setId_employe(int id_employe) {
        this.id_employe = id_employe;
    }

    @Override
    public String toString() {
        return "Reponse [message=" + message + ", id_employe=" + id_employe + "]";
    }

    public int getId_reponse() {
        return id_reponse;

    }

    public void setId_reponse(int id_reponse) {
        this.id_reponse = id_reponse;
    }
}
