package entities;

import java.sql.Time;
import java.util.Date;
import java.util.Timer;

public class Entretien {

    private int id;

    private String titre ;

    private String description ;

    private Date date_entretien ;

    private Time heure_entretien ;

    private TypeEntretien type_entretien;

    private boolean status ;

    public Entretien() {
    }

    public Entretien(String titre, String description, Date date_entretien, Time heure_entretien, TypeEntretien type_entretien, boolean status) {
        this.titre = titre;
        this.description = description;
        this.date_entretien = date_entretien;
        this.heure_entretien = heure_entretien;
        this.type_entretien = type_entretien;
        this.status = status;
    }


    @Override
    public String toString() {
        return "Entretien{" +
                "titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", heure_entretien=" + heure_entretien +
                ", date_entretien=" + date_entretien +
                ", type_entretien=" + type_entretien +
                ", status=" + status +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Date getDate_entretien() {
        return date_entretien;
    }

    public void setDate_entretien(Date date_entretien) {
        this.date_entretien = date_entretien;
    }

    public Time getHeure_entretien() {
        return heure_entretien;
    }

    public void setHeure_entretien(Time heure_entretien) {
        this.heure_entretien = heure_entretien;
    }

    public TypeEntretien getType_entretien() {
        return type_entretien;
    }

    public void setType_entretien(TypeEntretien type_entretien) {
        this.type_entretien = type_entretien;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
