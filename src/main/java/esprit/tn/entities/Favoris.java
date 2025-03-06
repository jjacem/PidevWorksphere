package esprit.tn.entities;

public class Favoris {
    private int id_favori;
    private User user;
    private int userId;
    private Formation formation;
    private int formationId;
    public Favoris() {}
    public Favoris(int id_favori, User user, int userId, Formation formation, int formationId) {
        this.id_favori = id_favori;
        this.user = user;
        this.userId = userId;
        this.formation = formation;
        this.formationId = formationId;
    }
    public Favoris( int userId, int formationId) {
        this.userId = userId;
        this.formationId = formationId;
    }

    public int getId_favori() {
        return id_favori;
    }

    public void setId_favori(int id_favori) {
        this.id_favori = id_favori;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public int getFormationId() {
        return formationId;
    }

    public void setFormationId(int formationId) {
        this.formationId = formationId;
    }
}
