package esprit.tn.entities;

import java.time.LocalDate;

public class Reservation {
    private int id_r;
    private LocalDate date;
    private User user;
    private int userId;
    private Formation formation;
    private int formationId;
    public Reservation() {};
    public Reservation(int id_r, LocalDate date, int userId, int formationId) {
        this.id_r = id_r;
        this.date = date;
        this.userId = userId;
        this.formationId = formationId;
    }
    public Reservation(LocalDate date, int userId, int formationId) {
        this.date = date;
        this.userId = userId;
        this.formationId = formationId;
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

    @Override
    public String toString() {
        return "Reservation{" +
                "id_r=" + id_r +
                ", date=" + date +
                ", userId=" + userId +
                ", formationId=" + formationId +
                '}';
    }
}
