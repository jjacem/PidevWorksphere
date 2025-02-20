package entities;

import java.util.Date;

public class Feedback {

    private int id;
    private String message ;
    private int rate ;
    private Date date_feedback;
    private int entretienId;

    public Feedback() {}

    @Override
    public String toString() {
        return "Feedback{" +
                "message='" + message + '\'' +
                ", rate=" + rate +
                ", date_feedback=" + date_feedback +
                ", entretienId=" + entretienId +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public Date getDate_feedback() {
        return date_feedback;
    }

    public void setDate_feedback(Date date_feedback) {
        this.date_feedback = date_feedback;
    }

    public int getEntretienId() {
        return entretienId;
    }

    public void setEntretienId(int entretienId) {
        this.entretienId = entretienId;
    }

    public Feedback(int id, String message, int rate, Date date_feedback, int entretienId) {
        this.id = id;
        this.message = message;
        this.rate = rate;
        this.date_feedback = date_feedback;
        this.entretienId = entretienId;
    }

    public Feedback(String message, int rate, int entretienId, Date date_feedback) {
        this.message = message;
        this.rate = rate;
        this.entretienId = entretienId;
        this.date_feedback = date_feedback;
    }
}
