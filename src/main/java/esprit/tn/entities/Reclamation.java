package esprit.tn.entities;

public class Reclamation {
private int id_reclamation,id_candidat;
private String status;
private String description;
private Reponse rep;
public Reclamation(String status, String description) {
    super();
    this.status = status;
    this.description = description;
}

    public int getId_reclamation() {
        return id_reclamation;
    }

    public int getId_candidat() {
        return id_candidat;
    }
    public void setId_candidat(int id_candidat) {
        this.id_candidat = id_candidat;
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
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
                ", description='" + description + '\'' +
                ", rep=" + rep +
                '}';
    }
}
