package esprit.tn.services;

import esprit.tn.entities.OffreEmploi;
import esprit.tn.utils.MyDatabase;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceOffre implements IService<OffreEmploi> {
    Connection connection;

    public ServiceOffre() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(OffreEmploi offre) throws SQLException {
        String req = "insert into offre (titre, description, type_contrat, salaire, lieu_travail, date_publication, date_limite, statut_offre, experience) " +
                "values('" + offre.getTitre() + "', '" + offre.getDescription() + "', '" + offre.getTypeContrat() + "', " +
                offre.getSalaire() + ", '" + offre.getLieuTravail() + "', '" + new java.sql.Date(offre.getDatePublication().getTime()) + "', '" +
                new java.sql.Date(offre.getDateLimite().getTime()) + "', '" + offre.getStatutOffre() + "', '" + offre.getExperience() + "')";

        Statement statement = connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("Offre ajoutée avec succès !");
    }

    @Override
    public void modifier(OffreEmploi offre) throws SQLException {
        String req = "update offre set titre=?, description=?, type_contrat=?, salaire=?, lieu_travail=?, date_publication=?, date_limite=?, statut_offre=?, experience=? where id_offre=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);

        preparedStatement.setString(1, offre.getTitre());
        preparedStatement.setString(2, offre.getDescription());
        preparedStatement.setString(3, offre.getTypeContrat());
        preparedStatement.setInt(4, offre.getSalaire());
        preparedStatement.setString(5, offre.getLieuTravail());
        preparedStatement.setDate(6, new java.sql.Date(offre.getDatePublication().getTime())); // Conversion LocalDate to SQL Date
        preparedStatement.setDate(7, new java.sql.Date(offre.getDateLimite().getTime())); // Conversion LocalDate to SQL Date
        preparedStatement.setString(8, offre.getStatutOffre());
        preparedStatement.setString(9, offre.getExperience());
        preparedStatement.setInt(10, offre.getIdOffre());

        preparedStatement.executeUpdate();
        System.out.println("Offre modifiée avec succès !");
    }


    @Override
    public void supprimer(int id) throws SQLException {
        String req = "delete from offre where id_offre=?";
        PreparedStatement preparedStatement = connection.prepareStatement(req);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        System.out.println("Offre supprimée avec succès !");
    }

    @Override
    public List<OffreEmploi> afficher() throws SQLException {
        List<OffreEmploi> offres = new ArrayList<>();
        String req = "select * from offre";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(req);

        while (rs.next()) {
            OffreEmploi offre = new OffreEmploi(
                rs.getInt("id_offre"),
                rs.getInt("salaire"),
                rs.getString("titre"),
                rs.getString("description"),
                rs.getString("type_contrat"),
                rs.getString("lieu_travail"),
                rs.getString("statut_offre"),
                rs.getString("experience"),
                rs.getDate("date_publication"),
                rs.getDate("date_limite")
            );

            offres.add(offre);
        }

        return offres;
    }

    public List<OffreEmploi> recupererOffres() throws SQLException {
        List<OffreEmploi> offres = new ArrayList<>();

        String query = "SELECT * FROM offre";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            OffreEmploi offre = new OffreEmploi(
                    resultSet.getInt("id_offre"),
                    resultSet.getInt("salaire"),
                    resultSet.getString("titre"),
                    resultSet.getString("description"),
                    resultSet.getString("type_contrat"),
                    resultSet.getString("lieu_travail"),
                    resultSet.getString("statut_offre"),
                    resultSet.getString("experience"),
                    resultSet.getDate("date_publication"),
                    resultSet.getDate("date_limite")
            );
            offres.add(offre);
        }

        return offres;
    }

    public void supprimerOffre(int id_offre) throws SQLException {
        String query = "DELETE FROM offre WHERE id_offre = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id_offre);
            int rowsAffected = ps.executeUpdate(); // Exécution de la requête
            if (rowsAffected > 0) {
                System.out.println("Offre supprimée avec succès de la base de données.");
            }else {
                System.out.println("Aucune offre correspondante trouvée pour la suppression.");
            }
            //ps.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'offre : " + e.getMessage());
        }
    }

    public OffreEmploi recupererOffreParTitre(String titreOffre) throws SQLException {
        String query = "SELECT * FROM offre WHERE titre = ?";
        OffreEmploi offre = null;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, titreOffre);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return new OffreEmploi(
                        rs.getInt("id_offre"),
                        rs.getInt("salaire"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("type_contrat"),
                        rs.getString("lieu_travail"),
                        rs.getString("statut_offre"),
                        rs.getString("experience"),
                        rs.getDate("date_publication"),
                        rs.getDate("date_limite")
                );
            }
        }catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'offre : " + e.getMessage());
        }
        return null;
    }

    public void modifierOffre(OffreEmploi offre) throws SQLException {
        String query = "UPDATE offre SET titre = ?, description = ?, type_contrat = ?, salaire = ?, lieu_travail = ?, experience = ?, statut_offre = ?, date_publication = ?, date_limite = ? WHERE id_offre = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, offre.getTitre());
            ps.setString(2, offre.getDescription());
            ps.setString(3, offre.getTypeContrat());
            ps.setDouble(4, offre.getSalaire());
            ps.setString(5, offre.getLieuTravail());
            ps.setString(6, offre.getExperience());
            ps.setString(7, offre.getStatutOffre());
            ps.setDate(8, new java.sql.Date(offre.getDatePublication().getTime()));
            ps.setDate(9, new java.sql.Date(offre.getDateLimite().getTime()));

            ps.setInt(10, offre.getIdOffre());

            ps.executeUpdate();
            System.out.println("Offre modifiée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<OffreEmploi> rechercherOffres(String searchTerm) throws SQLException {
        List<OffreEmploi> offres = new ArrayList<>();
        String query = "SELECT * FROM offre WHERE titre LIKE ? OR description LIKE ? OR lieu_travail LIKE ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OffreEmploi offre = new OffreEmploi(
                    rs.getInt("id_offre"),
                    rs.getInt("salaire"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    rs.getString("type_contrat"),
                    rs.getString("lieu_travail"),
                    rs.getString("statut_offre"),
                    rs.getString("experience"),
                    rs.getDate("date_publication"),
                    rs.getDate("date_limite")
                );
                offres.add(offre);
            }
        }
        return offres;
    }


    public OffreEmploi getOffreById(int idOffre) throws SQLException {
        String query = "SELECT * FROM offre WHERE id_offre = ?";
        OffreEmploi offre = null;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idOffre);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                offre = new OffreEmploi(
                        rs.getInt("id_offre"),
                        rs.getInt("salaire"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("type_contrat"),
                        rs.getString("lieu_travail"),
                        rs.getString("statut_offre"),
                        rs.getString("experience"),
                        rs.getDate("date_publication"),
                        rs.getDate("date_limite")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de l'offre par ID : " + e.getMessage());
        }

        return offre;
    }


















}

