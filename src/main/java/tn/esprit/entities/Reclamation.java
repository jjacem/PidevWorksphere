package tn.esprit.entities;

import java.sql.Timestamp;

public class Reclamation {
    private int id_reclamation;
    private int id_user;
    private int id_user2;
    private String status;
    private String titre;
    private String description;
    private String type;
    private Timestamp datedepot;
    private Reponse reponse;

    public Reponse getReponse() {
        return reponse;
    }

    public void setReponse(Reponse reponse) {
        this.reponse = reponse;
    }

    public Reclamation(String status, String titre, String description, String type, int id_user, int id_user2) {
        this.status = status;
        this.titre = titre;
        this.description = description;
        this.type = type;
        this.id_user = id_user;
        this.id_user2 = id_user2;
    }

    // Getter & Setter for datedepot
    public Timestamp getDatedepot() {
        return datedepot;
    }

    public void setDatedepot(Timestamp datedepot) {
        this.datedepot = datedepot;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id_reclamation=" + id_reclamation +
                ", status='" + status + '\'' +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", id_user=" + id_user +
                ", id_user2=" + id_user2 +
                ", datedepot=" + datedepot +
                '}';
    }

    public int getId_reclamation() {
        return id_reclamation;
    }

    public void setId_reclamation(int id_reclamation) {
        this.id_reclamation = id_reclamation;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId_user2() {
        return id_user2;
    }

    public void setId_user2(int id_user2) {
        this.id_user2 = id_user2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}