package db_lab.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public record User(
        String username,
        int totalVotes) {

    @Override
    public String toString() {
        return Printer.stringify(
                "User: ",
                List.of(Printer.field("username", this.username), Printer.field("totalVotes", this.totalVotes)));
    }

    public final class DAO {

        public static void signUpUser(Connection connection, String username, String password, String nome,
                String cognome, String email) {
            try (PreparedStatement statement = DAOUtils.prepare(connection, Queries.USER_SIGN_UP, username, password,
                    nome, cognome, email)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void createCollection(Connection connection, String nome, boolean privata, String username) {
            try (PreparedStatement statement = DAOUtils.prepare(connection, Queries.CREATE_COLLECTION, nome, privata,
                    username)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void voteCreation(Connection connection, int idInserzione, String username, boolean tipo) {
            try {
                connection.setAutoCommit(false);

                // Inserimento nella tabella Votazione
                try (PreparedStatement voteStatement = DAOUtils.prepare(connection, Queries.VOTE_CREATION, idInserzione,
                        username, tipo)) {
                    voteStatement.executeUpdate();
                }

                // Aggiornamento del numero di voti nella tabella UTENTE
                try (PreparedStatement updateStatement = DAOUtils.prepare(connection, Queries.UPDATE_TOTAL_VOTES, tipo,
                        tipo, idInserzione)) {
                    updateStatement.executeUpdate();
                }

                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException autoCommitEx) {
                    autoCommitEx.printStackTrace();
                }
            }
        }

        public static void commentCreation(Connection connection, int idInserzione, String username, String commento) {
            try (PreparedStatement statement = DAOUtils.prepare(connection, Queries.COMMENT_CREATION, idInserzione,
                    username, commento)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void reportInsertion(Connection connection, int idInserzione, String username, String motivo) {
            try {
                connection.setAutoCommit(false);

                // Inserimento nella tabella Segnalazione_Inserzione
                try (PreparedStatement reportStatement = DAOUtils.prepare(connection, Queries.REPORT_INSERTION,
                        idInserzione, username, motivo)) {
                    reportStatement.executeUpdate();
                }

                // Aggiornamento dello stato dell'utente
                String updateUserStatusQuery = String.format(Queries.UPDATE_USER_STATUS_TEMPLATE,
                        "Segnalazione_Inserzione", "IDinserzione");
                try (PreparedStatement updateStatement = DAOUtils.prepare(connection, updateUserStatusQuery,
                        idInserzione)) {
                    updateStatement.executeUpdate();
                }

                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException autoCommitEx) {
                    autoCommitEx.printStackTrace();
                }
            }
        }

        public static void reportComment(Connection connection, int idCommento, String username, String motivo) {
            try {
                connection.setAutoCommit(false);

                // Inserimento nella tabella Segnalazione_Commento
                try (PreparedStatement reportStatement = DAOUtils.prepare(connection, Queries.REPORT_COMMENT,
                        idCommento, username, motivo)) {
                    reportStatement.executeUpdate();
                }

                // Aggiornamento dello stato dell'utente
                String updateUserStatusQuery = String.format(Queries.UPDATE_USER_STATUS_TEMPLATE,
                        "Segnalazione_Commento", "IDcommento");
                try (PreparedStatement updateStatement = DAOUtils.prepare(connection, updateUserStatusQuery,
                        idCommento)) {
                    updateStatement.executeUpdate();
                }

                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException autoCommitEx) {
                    autoCommitEx.printStackTrace();
                }
            }
        }

        public static List<User> topCreators(Connection connection) {
            List<User> result = new ArrayList<>();
            String query = Queries.TOP_CREATORS;
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String username = resultSet.getString("Username");
                        int votiTotali = resultSet.getInt("VotiTotali");
                        result.add(new User(username, votiTotali));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }

        public static int createCharacter(Connection connection, String nome, String descrizione, int forza,
                int destrezza, int costituzione, int intelligenza, int saggezza, int carisma, int idRaccolta,
                String classe, String razza, int livello) {
            try {
                connection.setAutoCommit(false);

                // Inserimento nella tabella CREAZIONE
                try (PreparedStatement creationStatement = DAOUtils.prepareWithGeneratedKeys(connection,
                        Queries.CREATION_INSERT, nome, descrizione, forza, destrezza, costituzione, intelligenza,
                        saggezza, carisma, idRaccolta)) {
                    creationStatement.executeUpdate();

                    // Ottenere l'ID generato per la creazione
                    try (ResultSet generatedKeys = creationStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int idCreazione = generatedKeys.getInt(1);

                            // Inserimento nella tabella PERSONAGGIO
                            try (PreparedStatement characterStatement = DAOUtils.prepare(connection,
                                    Queries.CHARACTER_CREATION, idCreazione, classe, razza, livello)) {
                                characterStatement.executeUpdate();
                            }

                            connection.commit();
                            return idCreazione; // Restituisce l'ID della creazione
                        } else {
                            throw new SQLException("Creazione ID non ottenuto.");
                        }
                    }
                }
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                e.printStackTrace();
                return -1; // Indica un errore
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException autoCommitEx) {
                    autoCommitEx.printStackTrace();
                }
            }
        }

        public static int createMonster(Connection connection, String nome, String descrizione, int forza,
                int destrezza, int costituzione, int intelligenza, int saggezza, int carisma, int idRaccolta,
                String taglia, int difficolta, String tipo) {
            try {
                connection.setAutoCommit(false);

                // Inserimento nella tabella CREAZIONE
                try (PreparedStatement creationStatement = DAOUtils.prepareWithGeneratedKeys(connection,
                        Queries.CREATION_INSERT, nome, descrizione, forza, destrezza, costituzione, intelligenza,
                        saggezza, carisma, idRaccolta)) {
                    creationStatement.executeUpdate();

                    // Ottenere l'ID generato per la creazione
                    try (ResultSet generatedKeys = creationStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int idCreazione = generatedKeys.getInt(1);

                            // Inserimento nella tabella MOSTRO
                            try (PreparedStatement monsterStatement = DAOUtils.prepare(connection,
                                    Queries.MONSTER_CREATION, idCreazione, taglia, difficolta, tipo)) {
                                monsterStatement.executeUpdate();
                            }

                            connection.commit();
                            return idCreazione; // Restituisce l'ID della creazione
                        } else {
                            throw new SQLException("Creazione ID non ottenuto.");
                        }
                    }
                }
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                e.printStackTrace();
                return -1; // Indica un errore
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException autoCommitEx) {
                    autoCommitEx.printStackTrace();
                }
            }
        }

        public static List<Integer> getCollectionIdsByUsername(Connection connection, String username) {
            List<Integer> collectionIds = new ArrayList<>();
            String query = Queries.SELECT_COLLECTIONS_BY_USERNAME;

            try (PreparedStatement statement = DAOUtils.prepare(connection, query, username);
                    ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    collectionIds.add(resultSet.getInt("IDraccolta"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return collectionIds;
        }
    }
}
