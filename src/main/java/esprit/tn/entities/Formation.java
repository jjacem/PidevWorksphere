package esprit.tn.entities;

import java.time.LocalDate;
import java.util.Objects;

public class Formation {
    private int id_f;
    private String titre;
    private String description;
    private int nb_place;
    private Typeformation type;
    private int id_user;
    private LocalDate date;
    private String photo;
    private Certifie certifie;
    private String langue;

    public Formation() {}

    public Formation(int id_f, String titre, String description, int nb_place, Typeformation type,
                     int id_user, LocalDate date, String photo, Certifie certifie, String langue) {
        this.id_f = id_f;
        this.titre = titre;
        this.description = description;
        this.nb_place = nb_place;
        this.type = type;
        this.id_user = id_user;
        this.date = date;
        this.photo = photo;
        this.certifie = certifie;
        this.langue = langue;
    }

    public Formation(String titre, String description, int nb_place, Typeformation type,
                     int id_user, LocalDate date, String photo, Certifie certifie, String langue) {
        this.titre = titre;
        this.description = description;
        this.nb_place = nb_place;
        this.type = type;
        this.id_user = id_user;
        this.date = date;
        this.photo = photo;
        this.certifie = certifie;
        this.langue = langue;
    }

    // Getters and Setters

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

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Certifie getCertifie() {
        return certifie;
    }

    public void setCertifie(Certifie certifie) {
        this.certifie = certifie;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    @Override
    public String toString() {
        return "Formation{" +
                "id_f=" + id_f +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", nb_place=" + nb_place +
                ", type=" + type +
                ", id_user=" + id_user +
                ", date=" + date +
                ", photo='" + photo + '\'' +
                ", certifie=" + certifie +
                ", langue='" + langue + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Formation that)) return false;
        return id_f == that.id_f && nb_place == that.nb_place && id_user == that.id_user &&
                Objects.equals(titre, that.titre) &&
                Objects.equals(description, that.description) &&
                type == that.type &&
                Objects.equals(date, that.date) &&
                Objects.equals(photo, that.photo) &&
                certifie == that.certifie &&
                Objects.equals(langue, that.langue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_f, titre, description, nb_place, type, id_user, date, photo, certifie, langue);
    }
}