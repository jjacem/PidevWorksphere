package entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class Formation {
    private int id_f;
    private String titre, description;
    private LocalDate date;
    private LocalTime heure_debut, heure_fin;
    private int nb_place;
    private Typeformation type;
    private User user;
    private int userId;  // Déclaration du champ userId

    public Formation() {
    }

    public Formation(int id_f, String titre, String description, LocalDate date, LocalTime heure_debut, LocalTime heure_fin, int nb_place, Typeformation type , int userId) {
        this.id_f = id_f;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.heure_debut = heure_debut;
        this.heure_fin = heure_fin;
        this.nb_place = nb_place;
        this.type = type;
        this.userId = userId;
    }

    public Formation(String titre, String description, LocalDate date, LocalTime heure_debut, LocalTime heure_fin, int nb_place, Typeformation type , int userId) {
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.heure_debut = heure_debut;
        this.heure_fin = heure_fin;
        this.nb_place = nb_place;
        this.type = type;
        this.userId = userId;
    }

    public int getId_f() {
        return id_f;
    }

    public void setId_f(int id_f) {
        this.id_f = id_f;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeure_debut() {
        return heure_debut;
    }

    public void setHeure_debut(LocalTime heure_debut) {
        this.heure_debut = heure_debut;
    }

    public LocalTime getHeure_fin() {
        return heure_fin;
    }

    public void setHeure_fin(LocalTime heure_fin) {
        this.heure_fin = heure_fin;
    }

    public int getNb_place() {
        return nb_place;
    }

    public void setNb_place(int nb_place) {
        this.nb_place = nb_place;
    }

    public Typeformation getType() {
        return type;
    }

    public void setType(Typeformation type) {
        this.type = type;
    }


    // Getter et Setter pour userId
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Formation{" +
                "id_f=" + id_f +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", heure_debut=" + heure_debut +
                ", heure_fin=" + heure_fin +
                ", nb_place=" + nb_place +
                ", type=" + type +
                ", userId=" + userId +  // Affichage de userId dans toString
                '}';
    }
}
