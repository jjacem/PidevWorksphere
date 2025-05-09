package esprit.tn.entities;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Evenement {

    private int idEvent;
    private String nomEvent;
    private String descEvent;
    private LocalDateTime dateEvent;
    private String lieuEvent;
    private int capaciteEvent;
    private User RHgestionnaireEvent; // Nouvel attribut pour l'utilisateur qui gère l'événement
    private int id_user	;
    private String typeEvent;




    public Evenement(String nomEvent, String descEvent, LocalDateTime dateEvent, String lieuEvent, int capaciteEvent, User RHgestionnaireEvent, int id_user	) {
        this.nomEvent = nomEvent;
        this.descEvent = descEvent;
        this.dateEvent = dateEvent;
        this.lieuEvent = lieuEvent;
        this.capaciteEvent = capaciteEvent;
        this.RHgestionnaireEvent = RHgestionnaireEvent;
        this.typeEvent = typeEvent;
        this.id_user	 = id_user	;
    }

    public Evenement(String nomEvent, String descEvent, LocalDateTime dateEvent, String lieuEvent, int capaciteEvent, int id_user) {
        this.nomEvent = nomEvent;
        this.descEvent = descEvent;
        this.dateEvent = dateEvent;
        this.lieuEvent = lieuEvent;
        this.capaciteEvent = capaciteEvent;
        this.id_user = id_user;
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

    public LocalDateTime getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(LocalDateTime dateEvent) {
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

    public int getId_user	() {
        return id_user	;
    }

    public void setId_user	(int id_user	) {
        this.id_user	 = id_user	;
    }
    public String getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(String typeEvent) {
        this.typeEvent = typeEvent;
    }

    @Override
    public String toString() {
        return "Evenement{" +
                "nomEvent='" + nomEvent + '\'' +
                ", descEvent='" + descEvent + '\'' +
                ", dateEvent=" + dateEvent +
                ", lieuEvent='" + lieuEvent + '\'' +
                ", capaciteEvent=" + capaciteEvent +
                ", typeEvent='" + typeEvent + '\'' +
                ", id_user=" + id_user +
                ", RHgestionnaireEvent=" + (RHgestionnaireEvent != null ? RHgestionnaireEvent.getNom() + " " + RHgestionnaireEvent.getPrenom() : "Non assigné") +
                '}';
    }

}
