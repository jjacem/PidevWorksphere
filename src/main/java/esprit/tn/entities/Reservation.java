package esprit.tn.entities;

import java.time.LocalDate;

public class Reservation {
    private int id_r;
    private LocalDate date;
    private User user;
    private int userId;
    private Formation formation;
    private int formationId;
    private String motif;
    private String attente;
    private Langue lang;
    public Reservation() {};
    public Reservation(int id_r, LocalDate date, int userId, int formationId , String motif, String attente, Langue lang) {
        this.id_r = id_r;
        this.date = date;
        this.userId = userId;
        this.formationId = formationId;
        this.motif = motif;
        this.attente = attente;
        this.lang = lang;
    }
    public Reservation(LocalDate date, int userId, int formationId , String motif, String attente, Langue lang) {
        this.date = date;
        this.userId = userId;
        this.formationId = formationId;
        this.motif = motif;
        this.attente = attente;
        this.lang = lang;
    }


    public int getId_r() {
        return id_r;
    }

    public void setId_r(int id_r) {
        this.id_r = id_r;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFormationId() {
        return formationId;
    }

    public void setFormationId(int formationId) {
        this.formationId = formationId;
    }
    public User getUser() {
        return user;
    }
    public Formation getFormation() {return formation;}

    public void setUser(User user) {
        this.user = user;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getAttente() {
        return attente;
    }

    public void setAttente(String attente) {
        this.attente = attente;
    }

    public Langue getLang() {
        return lang;
    }

    public void setLang(Langue lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id_r=" + id_r +
                ", date=" + date +
                ", user=" + user +
                ", userId=" + userId +
                ", formation=" + formation +
                ", formationId=" + formationId +
                ", motif='" + motif + '\'' +
                ", attente='" + attente + '\'' +
                ", lang=" + lang +
                '}';
    }
}
