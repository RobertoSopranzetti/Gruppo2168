package db_lab.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public record Admin() {

    public final class DAO {

        public static void signUpAdmin(Connection connection, String username, String password, String nome,
                String cognome, String email) {
            try (PreparedStatement statement = DAOUtils.prepare(connection, Queries.ADMIN_SIGN_UP, username, password,
                    nome, cognome, email)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static List<String> badUsers(Connection connection) {
            List<String> result = new ArrayList<>();
            String query = Queries.SHOW_BAD_USERS;
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        result.add(resultSet.getString("Username"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }

        public static List<String> reportedUsers(Connection connection) {
            List<String> result = new ArrayList<>();
            String query = Queries.SHOW_REPORTED_USERS;
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        result.add(resultSet.getString("Username"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }

        public static void userModeration(Connection connection, String adminName, String username, String tipo) {
            try {
                // Inizia una transazione
                connection.setAutoCommit(false);

                // Inserisci la moderazione
                try (PreparedStatement statement = DAOUtils.prepare(connection, Queries.USER_MODERATION, adminName,
                        username, tipo)) {
                    statement.executeUpdate();
                }

                // Aggiorna lo stato di segnalazione dell'utente
                try (PreparedStatement statement = DAOUtils.prepare(connection, Queries.USER_REPORT_UPDATE, username)) {
                    statement.executeUpdate();
                }

                // Conferma la transazione
                connection.commit();
            } catch (SQLException e) {
                try {
                    // Annulla la transazione in caso di errore
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                try {
                    // Ripristina l'auto-commit
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public static void addSubcategory(Connection connection, String nome, String descrizione, String username) {
            try (PreparedStatement statement = DAOUtils.prepare(connection, Queries.ADD_SUBCATEGORY, nome, descrizione,
                    username)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void addToSubcategory(Connection connection, int idInserzione, int idSottocategoria) {
            try (PreparedStatement statement = DAOUtils.prepare(connection, Queries.ADD_TO_SUBCATEGORY, idInserzione,
                    idSottocategoria)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}