package tn.esprit.entities;


import java.sql.Timestamp;

public class Reponse {
    private int id_reponse;
    private String message;
    private Timestamp datedepot;
    private String status;
    private int id_user;
    private int id_reclamation;

    public Reponse(String message, int id_user, int id_reclamation, String status) {
        this.message = message;
        this.id_user = id_user;
        this.id_reclamation = id_reclamation;
        this.status = status;
    }

    public int getId_reponse() {
        return id_reponse;
    }

    public void setId_reponse(int id_reponse) {
        this.id_reponse = id_reponse;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getDatedepot() {
        return datedepot;
    }

    public void setDatedepot(Timestamp datedepot) {
        this.datedepot = datedepot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId_reclamation() {
        return id_reclamation;
    }

    public void setId_reclamation(int id_reclamation) {
        this.id_reclamation = id_reclamation;
    }

    @Override
    public String toString() {
        return "Reponse{" +
                "id_reponse=" + id_reponse +
                ", message='" + message + '\'' +
                ", datedepot=" + datedepot +
                ", status='" + status + '\'' +
                ", id_user=" + id_user +
                ", id_reclamation=" + id_reclamation +
                '}';
    }
}