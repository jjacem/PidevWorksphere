package esprit.tn.entities;

import java.sql.Time;
import java.util.Date;

public class Entretien {

    private int id;

    private String titre ;

    private String description ;

    private Date date_entretien ;

    private Time heure_entretien ;

    private TypeEntretien type_entretien;

    private boolean status ;


    private int candidatId;

    private int employeId ;

    private int feedbackId ;

    private int idOffre;

    private int idCandidature;



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


    public Entretien(int id, String titre, String description, Date date_entretien, Time heure_entretien, TypeEntretien type_entretien, boolean status, int feedbackId) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date_entretien = date_entretien;
        this.heure_entretien = heure_entretien;
        this.type_entretien = type_entretien;
        this.status = status;
        this.feedbackId = feedbackId;
    }


    public Entretien(int id, String titre, String description, Date date_entretien, Time heure_entretien, TypeEntretien type_entretien, boolean status, int feedbackId , int employeId) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date_entretien = date_entretien;
        this.heure_entretien = heure_entretien;
        this.type_entretien = type_entretien;
        this.status = status;
        this.feedbackId = feedbackId;
        this.employeId = employeId;
    }



    public Entretien(int id, String titre, String description, Date date_entretien, Time heure_entretien, TypeEntretien type_entretien, boolean status) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date_entretien = date_entretien;
        this.heure_entretien = heure_entretien;
        this.type_entretien = type_entretien;
        this.status = status;
    }


    public Entretien(String titre, String description, Date date_entretien, Time heure_entretien, TypeEntretien type_entretien, boolean status, int candidatId, int employeId, int feedbackId, int idOffre, int idCandidature) {
        this.titre = titre;
        this.description = description;
        this.date_entretien = date_entretien;
        this.heure_entretien = heure_entretien;
        this.type_entretien = type_entretien;
        this.status = status;
        this.candidatId = candidatId;
        this.employeId = employeId;
        this.feedbackId = feedbackId;
        this.idOffre = idOffre;
        this.idCandidature = idCandidature;
    }

    public Entretien(int id, String titre, String description, Date date_entretien, Time heure_entretien, TypeEntretien type_entretien, boolean status, int candidatId, int employeId, int feedbackId, int idOffre, int idCandidature) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date_entretien = date_entretien;
        this.heure_entretien = heure_entretien;
        this.type_entretien = type_entretien;
        this.status = status;
        this.candidatId = candidatId;
        this.employeId = employeId;
        this.feedbackId = feedbackId;
        this.idOffre = idOffre;
        this.idCandidature = idCandidature;
    }

    public int getEmployeId() {
        return employeId;
    }

    public void setEmployeId(int employeId) {
        this.employeId = employeId;
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


    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getCandidatId() {
        return candidatId;
    }

    public void setCandidatId(int candidatId) {
        this.candidatId = candidatId;
    }

    public int getIdOffre() {
        return idOffre;
    }

    public void setIdOffre(int idOffre) {
        this.idOffre = idOffre;
    }

    public int getIdCandidature() {
        return idCandidature;
    }

    public void setIdCandidature(int idCandidature) {
        this.idCandidature = idCandidature;
    }


    @Override
    public String toString() {
        return "Entretien{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", date_entretien=" + date_entretien +
                ", heure_entretien=" + heure_entretien +
                ", type_entretien=" + type_entretien +
                ", status=" + status +
                ", candidatId=" + candidatId +
                ", employeId=" + employeId +
                ", feedbackId=" + feedbackId +
                ", idOffre=" + idOffre +
                ", idCandidature=" + idCandidature +
                '}';
    }
}
