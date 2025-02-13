package tn.esprit.entities;


public class User {
    private int idU;
    private String nomU, prenomU, mdpU, emailU;
    private Role role;


    public User() {
    }

    public User(int idU, String nomU, String prenomU, String mdpU, String emailU, Role role) {
        this.idU = idU;
        this.nomU = nomU;
        this.prenomU = prenomU;
        this.mdpU = mdpU;
        this.emailU = emailU;
        this.role = role;
    }

    public User(String nomU, String prenomU, String mdpU, String emailU, Role role) {
        this.nomU = nomU;
        this.prenomU = prenomU;
        this.mdpU = mdpU;
        this.emailU = emailU;
        this.role = role;
    }


    public int getIdU() {
        return idU;
    }

    public void setIdU(int idU) {
        this.idU = idU;
    }

    public String getNomU() {
        return nomU;
    }

    public void setNomU(String nomU) {
        this.nomU = nomU;
    }

    public String getPrenomU() {
        return prenomU;
    }

    public void setPrenomU(String prenomU) {
        this.prenomU = prenomU;
    }

    public String getMdpU() {
        return mdpU;
    }

    public void setMdpU(String mdpU) {
        this.mdpU = mdpU;
    }

    public String getEmailU() {
        return emailU;
    }

    public void setEmailU(String emailU) {
        this.emailU = emailU;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "idU=" + idU +
                ", nomU='" + nomU + '\'' +
                ", prenomU='" + prenomU + '\'' +
                ", mdpU='" + mdpU + '\'' +
                ", emailU='" + emailU + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
