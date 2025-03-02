package esprit.tn.entities;

import java.sql.Date;

public class EvenementSponsor {
    private int evenementId;
    private int sponsorId;
    private Date datedebutContrat;
    private DureeContrat duree;

    // Constructeur
    public EvenementSponsor(int evenementId, int sponsorId, Date datedebutContrat, DureeContrat duree) {
        this.evenementId = evenementId;
        this.sponsorId = sponsorId;
        this.datedebutContrat = datedebutContrat;
        this.duree = duree;
    }

    // Getters et Setters
    public int getEvenementId() {
        return evenementId;
    }

    public void setEvenementId(int evenementId) {
        this.evenementId = evenementId;
    }

    public int getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(int sponsorId) {
        this.sponsorId = sponsorId;
    }

    public Date getDatedebutContrat() {
        return datedebutContrat;
    }

    public void setDatedebutContrat(Date datedebutContrat) {
        this.datedebutContrat = datedebutContrat;
    }

    public String getDuree() {
        return duree.name(); // Retourne le nom de l'énumération (par exemple, "troisMois")
    }

    public void setDuree(DureeContrat duree) {
        this.duree = duree;
    }

    @Override
    public String toString() {
        return "EvenementSponsor{" +
                "evenementId=" + evenementId +
                ", sponsorId=" + sponsorId +
                ", datedebutContrat=" + datedebutContrat +
                ", duree='" + duree + '\'' +
                '}';
    }
}