package esprit.tn.entities;

import java.sql.Timestamp;

public class HistoriqueEntretien {

    private int id;
    private String titre;
    private String description;
    private String dateEntretien;
    private String heureEntretien;
    private String typeEntretien;
    private boolean status;
    private String action;
    private Timestamp dateAction;
    private int employeId;
    private int feedbackId;
    private int candidatId;
    private int idOffre;
    private int idCandidature;
    private int entretienId;

    public HistoriqueEntretien(int id, String titre, String description, String dateEntretien, String heureEntretien,
                               String typeEntretien, boolean status, String action, Timestamp dateAction,
                               int employeId, int feedbackId, int candidatId, int idOffre, int idCandidature, int entretienId) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dateEntretien = dateEntretien;
        this.heureEntretien = heureEntretien;
        this.typeEntretien = typeEntretien;
        this.status = status;
        this.action = action;
        this.dateAction = dateAction;
        this.employeId = employeId;
        this.feedbackId = feedbackId;
        this.candidatId = candidatId;
        this.idOffre = idOffre;
        this.idCandidature = idCandidature;
        this.entretienId = entretienId;
    }


    public HistoriqueEntretien(String titre, String description, String dateEntretien, String heureEntretien, String typeEntretien, boolean status, String action, Timestamp dateAction, int employeId, int feedbackId, int candidatId, int idOffre, int idCandidature, int entretienId) {
        this.titre = titre;
        this.description = description;
        this.dateEntretien = dateEntretien;
        this.heureEntretien = heureEntretien;
        this.typeEntretien = typeEntretien;
        this.status = status;
        this.action = action;
        this.dateAction = dateAction;
        this.employeId = employeId;
        this.feedbackId = feedbackId;
        this.candidatId = candidatId;
        this.idOffre = idOffre;
        this.idCandidature = idCandidature;
        this.entretienId = entretienId;
    }


    public HistoriqueEntretien(String titre, String description, String dateEntretien, String heureEntretien, String typeEntretien, boolean status, String action, Timestamp dateAction, int employeId, int feedbackId, int candidatId, int idOffre, int idCandidature) {
        this.titre = titre;
        this.description = description;
        this.dateEntretien = dateEntretien;
        this.heureEntretien = heureEntretien;
        this.typeEntretien = typeEntretien;
        this.status = status;
        this.action = action;
        this.dateAction = dateAction;
        this.employeId = employeId;
        this.feedbackId = feedbackId;
        this.candidatId = candidatId;
        this.idOffre = idOffre;
        this.idCandidature = idCandidature;
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

    public String getDateEntretien() {
        return dateEntretien;
    }

    public void setDateEntretien(String dateEntretien) {
        this.dateEntretien = dateEntretien;
    }

    public String getHeureEntretien() {
        return heureEntretien;
    }

    public void setHeureEntretien(String heureEntretien) {
        this.heureEntretien = heureEntretien;
    }

    public String getTypeEntretien() {
        return typeEntretien;
    }

    public void setTypeEntretien(String typeEntretien) {
        this.typeEntretien = typeEntretien;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Timestamp getDateAction() {
        return dateAction;
    }

    public void setDateAction(Timestamp dateAction) {
        this.dateAction = dateAction;
    }

    public int getEmployeId() {
        return employeId;
    }

    public void setEmployeId(int employeId) {
        this.employeId = employeId;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
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

    public int getEntretienId() {
        return entretienId;
    }

    public void setEntretienId(int entretienId) {
        this.entretienId = entretienId;
    }

    @Override
    public String toString() {
        return "HistoriqueEntretien{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", dateEntretien='" + dateEntretien + '\'' +
                ", heureEntretien='" + heureEntretien + '\'' +
                ", typeEntretien='" + typeEntretien + '\'' +
                ", status=" + status +
                ", action='" + action + '\'' +
                ", dateAction=" + dateAction +
                ", employeId=" + employeId +
                ", feedbackId=" + feedbackId +
                ", candidatId=" + candidatId +
                ", idOffre=" + idOffre +
                ", idCandidature=" + idCandidature +
                ", entretienId=" + entretienId +
                '}';
    }
}



