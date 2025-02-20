package esprit.tn.entities;


public class Candidature {
    private int idCandidature;
    private String  cv, lettreMotivation;
    private OffreEmploi idOffre;
    private int idCandidat;


    public Candidature() {
    }

    public Candidature(int idCandidature, OffreEmploi idOffre, int idCandidat, String cv, String lettreMotivation) {
        this.idCandidature = idCandidature;
        this.idOffre = idOffre;
        this.idCandidat = idCandidat;
        this.cv = cv;
        this.lettreMotivation = lettreMotivation;
    }

    public Candidature(OffreEmploi idOffre, User idCandidat, String cv, String lettreMotivation) {
        this.idOffre = idOffre;
        this.idCandidat = idCandidat.getIdUser();
        this.cv = cv;
        this.lettreMotivation = lettreMotivation;
    }

    public int getIdCandidature() {
        return idCandidature;
    }

    public void setIdCandidature(int idCandidature) {
        this.idCandidature = idCandidature;
    }

    public OffreEmploi getIdOffre() {
        return idOffre;
    }

    public void setIdOffre(OffreEmploi idOffre) {
        this.idOffre = idOffre;
    }

    public int getIdCandidat() {
        return idCandidat;
    }

    public void setIdCandidat(int idCandidat) {
        this.idCandidat = idCandidat;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public String getLettreMotivation() {
        return lettreMotivation;
    }

    public void setLettreMotivation(String lettreMotivation) {
        this.lettreMotivation = lettreMotivation;
    }

    @Override
    public String toString() {
        return "Candidature{" +
                "idCandidature=" + idCandidature +
                ", idOffre=" + idOffre +
                ", idCandidat=" + idCandidat +
                ", cv='" + cv + '\'' +
                ", lettreMotivation='" + lettreMotivation + '\'' +
                '}';
    }
}
