package esprit.tn.entities;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;

public class Formation {
    private int id_f;
    private String titre, description;
    private LocalDate date;
    private LocalTime heure_debut, heure_fin;
    private int nb_place;
    private Typeformation type;
    private String photo;
    private User user;
    private int id_user;

    public Formation() {}

    public Formation(int id_f, String titre, String description, LocalDate date, LocalTime heure_debut,
                     LocalTime heure_fin, int nb_place, Typeformation type , String photo  , User user ) {
        this.id_f = id_f;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.heure_debut = heure_debut;
        this.heure_fin = heure_fin;
        this.nb_place = nb_place;
        this.type = type;
        this.photo = photo;
        this.user = user;

    }



    public Formation(String titre, String description, LocalDate date, LocalTime heure_debut,
                     LocalTime heure_fin, int nb_place, Typeformation type , String photo , User user ) {
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.heure_debut = heure_debut;
        this.heure_fin = heure_fin;
        this.nb_place = nb_place;
        this.type = type;
        this.photo = photo;
        this.user = user;

    }
    public Formation(String titre, String description, LocalDate date, LocalTime heure_debut,
                     LocalTime heure_fin, int nb_place, Typeformation type, String photo , int id_user ) {
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.heure_debut = heure_debut;
        this.heure_fin = heure_fin;
        this.nb_place = nb_place;
        this.type = type;
        this.photo = photo;
        this.id_user = id_user;


    }
    public Formation( int id_f ,String titre, String description, LocalDate date, LocalTime heure_debut,
                      LocalTime heure_fin, int nb_place, Typeformation type, String photo, int id_user ) {
        this.id_f = id_f;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.heure_debut = heure_debut;
        this.heure_fin = heure_fin;
        this.nb_place = nb_place;
        this.type = type;
        this.photo = photo;
        this.id_user = id_user;


    }

    public Formation(String titre, String description, LocalDate date, LocalTime heureDebut, LocalTime heureFin, int nbPlace, Typeformation type , String photo) {
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.heure_debut = heure_debut;
        this.heure_fin = heure_fin;
        this.nb_place = nb_place;
        this.type = type;
        this.photo = photo;

    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
                ", photo=" + photo +
                ", user=" + user +
                ", id_user=" + id_user +
                '}';
    }
}
