package tn.esprit.entities;

import java.time.LocalDate;
import java.util.Date;

public class OffreEmploi {
    private int idOffre, salaire;
    private String titre, description, typeContrat, lieuTravail, statutOffre, experience;
    private Date datePublication, dateLimite;

    public OffreEmploi(int id, String titre, String description, String typeContrat, String lieuTravail, int salaire, String statutOffre, int experience, LocalDate datePublication, LocalDate dateLimite) {
    }

    public OffreEmploi(int idOffre, int salaire, String titre, String description, String typeContrat, String lieuTravail, String statutOffre, String experience, Date datePublication, Date dateLimite) {
        this.idOffre = idOffre;
        this.salaire = salaire;
        this.titre = titre;
        this.description = description;
        this.typeContrat = typeContrat;
        this.lieuTravail = lieuTravail;
        this.statutOffre = statutOffre;
        this.experience = experience;
        this.datePublication = datePublication;
        this.dateLimite = dateLimite;
    }

    public OffreEmploi(int salaire, String titre, String description, String typeContrat, String lieuTravail, String statutOffre, String experience, Date datePublication, Date dateLimite) {
        this.salaire = salaire;
        this.titre = titre;
        this.description = description;
        this.typeContrat = typeContrat;
        this.lieuTravail = lieuTravail;
        this.statutOffre = statutOffre;
        this.experience = experience;
        this.datePublication = datePublication;
        this.dateLimite = dateLimite;
    }

    public OffreEmploi() {
    }

    public int getIdOffre() {
        return idOffre;
    }

    public void setIdOffre(int idOffre) {
        this.idOffre = idOffre;
    }

    public int getSalaire() {
        return salaire;
    }

    public void setSalaire(int salaire) {
        this.salaire = salaire;
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

    public String getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
    }

    public String getLieuTravail() {
        return lieuTravail;
    }

    public void setLieuTravail(String lieuTravail) {
        this.lieuTravail = lieuTravail;
    }

    public String getStatutOffre() {
        return statutOffre;
    }

    public void setStatutOffre(String statutOffre) {
        this.statutOffre = statutOffre;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public Date getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Date datePublication) {
        this.datePublication = datePublication;
    }

    public Date getDateLimite() {
        return dateLimite;
    }

    public void setDateLimite(Date dateLimite) {
        this.dateLimite = dateLimite;
    }

    @Override
    public String toString() {
        return "OffreEmploi{" +
                "idOffre=" + idOffre +
                ", salaire=" + salaire +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", typeContrat='" + typeContrat + '\'' +
                ", lieuTravail='" + lieuTravail + '\'' +
                ", statutOffre='" + statutOffre + '\'' +
                ", experience='" + experience + '\'' +
                ", datePublication=" + datePublication +
                ", dateLimite=" + dateLimite +
                '}';
    }
}
