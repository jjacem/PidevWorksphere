package entities;

import java.util.List;

public class Equipe {

    private int id;
    private String nomEquipe;
    private List<Employee> employes;

    public Equipe() {}

    public Equipe(int id, String nomEquipe, List<Employee> employes) {
        this.id = id;
        this.nomEquipe = nomEquipe;
        this.employes = employes;
    }



    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public List<Employee> getEmployes() {

        return employes;
    }
    public void setEmployes(List<Employee> employes) {

        this.employes = employes;
    }

    public String getNomEquipe() {

        return nomEquipe;
    }

    public void setNomEquipe(String nomEquipe) {

        this.nomEquipe = nomEquipe;
    }


    @Override
    public String toString() {
        return "Equipe {\n" +
                "  ID        : " + id + "\n" +
                "  Nom       : " + nomEquipe + "\n" +
                "  Employés  : " + employes + "\n" +
                "}";
    }

}
