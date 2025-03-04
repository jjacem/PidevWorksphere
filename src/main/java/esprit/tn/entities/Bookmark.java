package esprit.tn.entities;

import java.sql.Timestamp;

public class Bookmark {
    private int id;
    private int userId;
    private int offreId;
    private Timestamp dateCreated;

    public Bookmark() {
    }

    public Bookmark(int userId, int offreId) {
        this.userId = userId;
        this.offreId = offreId;
    }

    public Bookmark(int id, int userId, int offreId, Timestamp dateCreated) {
        this.id = id;
        this.userId = userId;
        this.offreId = offreId;
        this.dateCreated = dateCreated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getOffreId() {
        return offreId;
    }

    public void setOffreId(int offreId) {
        this.offreId = offreId;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "id=" + id +
                ", userId=" + userId +
                ", offreId=" + offreId +
                ", dateCreated=" + dateCreated +
                '}';
    }
}
