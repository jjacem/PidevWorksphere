package esprit.tn.entities;

import java.util.Date;
import java.util.List;

public class Evenement {

    private int idEvent;
    private String nomEvent;
    private String descEvent;
    private Date dateEvent;
    private String lieuEvent;
    private int capaciteEvent;
    private User RHgestionnaireEvent; // Nouvel attribut pour l'utilisateur qui gère l'événement
    private int RHgestionnaireId;




    public Evenement(String nomEvent, String descEvent, Date dateEvent, String lieuEvent, int capaciteEvent, User RHgestionnaireEvent, int RHgestionnaireId) {
        this.nomEvent = nomEvent;
        this.descEvent = descEvent;
        this.dateEvent = dateEvent;
        this.lieuEvent = lieuEvent;
        this.capaciteEvent = capaciteEvent;
        this.RHgestionnaireEvent = RHgestionnaireEvent;
        this.RHgestionnaireId = RHgestionnaireId;
    }

    public int getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(int idEvent) {
        this.idEvent = idEvent;
    }

    public String getNomEvent() {
        return nomEvent;
    }

    public void setNomEvent(String nomEvent) {
        this.nomEvent = nomEvent;
    }

    public String getDescEvent() {
        return descEvent;
    }

    public void setDescEvent(String descEvent) {
        this.descEvent = descEvent;
    }

    public Date getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(Date dateEvent) {
        this.dateEvent = dateEvent;
    }

    public String getLieuEvent() {
        return lieuEvent;
    }

    public void setLieuEvent(String lieuEvent) {
        this.lieuEvent = lieuEvent;
    }

    public int getCapaciteEvent() {
        return capaciteEvent;
    }

    public void setCapaciteEvent(int capaciteEvent) {
        this.capaciteEvent = capaciteEvent;
    }

    public User getRHgestionnaireEvent() {
        return RHgestionnaireEvent;
    }

    public void setRHgestionnaireEvent(User RHgestionnaireEvent) {
        this.RHgestionnaireEvent = RHgestionnaireEvent;
    }

    public int getRHgestionnaireId() {
        return RHgestionnaireId;
    }

    public void setRHgestionnaireId(int RHgestionnaireId) {
        this.RHgestionnaireId = RHgestionnaireId;
    }
}
