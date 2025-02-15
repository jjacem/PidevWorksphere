package esprit.tn.entities;

public class Reclamation {
private int id_reclamation,id_user;
private String status;
private String message_rec;
private Reponse rep;

public Reclamation(String status, String description) {
    super();
    this.status = status;
    this.message_rec = description;
}

    public int getId_reclamation() {
        return id_reclamation;
    }

    public int getId_candidat() {
        return id_user;
    }
    public void setId_candidat(int id_candidat) {
        this.id_user = id_candidat;
    }

    public void setId_reclamation(int id_reclamation) {
        this.id_reclamation = id_reclamation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return message_rec;
    }

    public void setDescription(String description) {
        this.message_rec = description;
    }

    public Reponse getRep() {
        return rep;
    }

    public void setRep(Reponse rep) {
        this.rep = rep;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id_reclamation=" + id_reclamation +
                ", status='" + status + '\'' +
                ", description='" + message_rec + '\'' +
                ", rep=" + rep +
                '}';
    }
}
