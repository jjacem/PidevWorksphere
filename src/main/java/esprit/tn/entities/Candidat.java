package esprit.tn.entities;

import esprit.tn.entities.User;
import jdk.jshell.Snippet;

import java.util.ArrayList;

public class Candidat extends User {
    private int idCandidat;
    private Status status;
    private double salaireAttendu;
    private ArrayList<Reclamation> reclamations;
    public void addReclamation(Reclamation r){
        r.setId_candidat(this.idCandidat);
        reclamations.add(r);
    }
    public void removeReclamation(Reclamation r){
        reclamations.remove(r);
    }
    public boolean updateReclamation(Reclamation r){
        for (Reclamation reclamation : reclamations) {
            if(reclamation.getId_reclamation() == r.getId_reclamation()){
                reclamations.set(reclamations.indexOf(reclamation), r);
                return true;
            }
        }
        return false;
    }
    public Reclamation searchreclamation(int id){
        for (Reclamation reclamation : reclamations) {
            if(reclamation.getId_reclamation() == id){
                return reclamation;
            }
        }
        return null;
    }

    public Candidat(String nom, String prenom, String email, String mdp, String adresse, Sexe sexe, double salaireAttendu) {
        super(nom,  prenom,  email,  mdp,  Role.CANDIDAT,  adresse,  sexe);

        this.status = Status.Candidature;
        this.salaireAttendu = salaireAttendu;
    }
public Candidat(User u, Status status, double salaireAttendu) {
        super(u.getNom(), u.getPrenom(), u.getEmail(), u.getMdp(), u.getRole(), u.getAdresse(), u.getSexe());

        this.idCandidat = idCandidat;
        this.status = status;
        this.salaireAttendu = salaireAttendu;
    }
    public Candidat(String nom, String prenom, String email, String mdp, String adresse, Sexe sexe, Status status, double salaireAttendu) {
        super(nom,  prenom,  email,  mdp,  Role.CANDIDAT,  adresse,  sexe);

        this.status = status;
        this.salaireAttendu = salaireAttendu;
    }

    public int getIdCandidat() {
        return idCandidat;
    }

    public void setIdCandidat(int idCandidat) {
        this.idCandidat = idCandidat;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getSalaireAttendu() {
        return salaireAttendu;
    }

    public void setSalaireAttendu(double salaireAttendu) {
        this.salaireAttendu = salaireAttendu;
    }

    @Override
    public String toString() {
        return super.toString()+"Candidat{" +
                "idCandidat=" + idCandidat +

                ", status='" + status + '\'' +
                ", salaireAttendu=" + salaireAttendu +
                '}';
    }
}
