package db_lab.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public record Creation(
        int idCreation,
        Date creationDate,
        String name,
        String description,
        int strength,
        int dexterity,
        int constitution,
        int intelligence,
        int wisdom,
        int charisma) implements CreationInterface {

    @Override
    public String toString() {
        return Printer.stringify("Creation: ", List.of(
                Printer.field("idCreation", this.idCreation),
                Printer.field("name", this.name),
                Printer.field("description", this.description),
                Printer.field("strength", this.strength),
                Printer.field("dexterity", this.dexterity),
                Printer.field("constitution", this.constitution),
                Printer.field("intelligence", this.intelligence),
                Printer.field("wisdom", this.wisdom),
                Printer.field("charisma", this.charisma)));
    }

    public static final class DAO {

        public static void publishCreation(Connection connection, int idCreazione, String username) {
            try (PreparedStatement statement = DAOUtils.prepare(connection, Queries.PUBLISH_CREATION, idCreazione,
                    username)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static List<CreationInterface> getCategory(Connection connection, int idCreation) {
            List<CreationInterface> relatedCreations = new ArrayList<>();
            String category = null;

            try {
                connection.setAutoCommit(false); // Inizia la transazione

                // Determina la categoria dell'inserzione
                try (PreparedStatement statement = connection.prepareStatement(Queries.DETERMINE_CATEGORY)) {
                    statement.setInt(1, idCreation);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            category = resultSet.getString("Categoria");
                        }
                    }
                }

                // Cerca altre inserzioni simili
                if ("Personaggio".equals(category)) {
                    try (PreparedStatement statement = connection.prepareStatement(Queries.GET_OTHER_CHARACTERS)) {
                        statement.setInt(1, idCreation);
                        try (ResultSet resultSet = statement.executeQuery()) {
                            while (resultSet.next()) {
                                int id = resultSet.getInt("IDcreazione");
                                Date creationDate = resultSet.getDate("dataCreazione");
                                String name = resultSet.getString("Nome");
                                String description = resultSet.getString("Descrizione");
                                int strength = resultSet.getInt("Forza");
                                int dexterity = resultSet.getInt("Destrezza");
                                int constitution = resultSet.getInt("Costituzione");
                                int intelligence = resultSet.getInt("Intelligenza");
                                int wisdom = resultSet.getInt("Saggezza");
                                int charisma = resultSet.getInt("Carisma");
                                String classType = resultSet.getString("Classe");
                                String race = resultSet.getString("Razza");
                                int level = resultSet.getInt("Livello");
                                Creation creation = new Creation(id, creationDate, name, description, strength,
                                        dexterity, constitution, intelligence, wisdom, charisma);
                                relatedCreations.add(new Character(creation, classType, race, level));
                            }
                        }
                    }
                } else if ("Mostro".equals(category)) {
                    try (PreparedStatement statement = connection.prepareStatement(Queries.GET_OTHER_MONSTERS)) {
                        statement.setInt(1, idCreation);
                        try (ResultSet resultSet = statement.executeQuery()) {
                            while (resultSet.next()) {
                                int id = resultSet.getInt("IDcreazione");
                                Date creationDate = resultSet.getDate("dataCreazione");
                                String name = resultSet.getString("Nome");
                                String description = resultSet.getString("Descrizione");
                                int strength = resultSet.getInt("Forza");
                                int dexterity = resultSet.getInt("Destrezza");
                                int constitution = resultSet.getInt("Costituzione");
                                int intelligence = resultSet.getInt("Intelligenza");
                                int wisdom = resultSet.getInt("Saggezza");
                                int charisma = resultSet.getInt("Carisma");
                                String size = resultSet.getString("Taglia");
                                int challengeRating = resultSet.getInt("GradoSfida");
                                String type = resultSet.getString("Tipo");
                                Creation creation = new Creation(id, creationDate, name, description, strength,
                                        dexterity, constitution, intelligence, wisdom, charisma);
                                relatedCreations.add(new Monster(creation, size, challengeRating, type));
                            }
                        }
                    }
                }

                connection.commit(); // Commit della transazione
            } catch (SQLException e) {
                try {
                    connection.rollback(); // Rollback della transazione in caso di errore
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                try {
                    connection.setAutoCommit(true); // Ripristina l'auto-commit
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            return relatedCreations;
        }

        public static List<CreationInterface> topCreations(Connection connection) {
            List<CreationInterface> result = new ArrayList<>();
            String query = Queries.TOP_CREATIONS;

            try (PreparedStatement statement = connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("IDcreazione");
                    Date creationDate = resultSet.getDate("dataCreazione");
                    String name = resultSet.getString("Nome");
                    String description = resultSet.getString("Descrizione");
                    int strength = resultSet.getInt("Forza");
                    int dexterity = resultSet.getInt("Destrezza");
                    int constitution = resultSet.getInt("Costituzione");
                    int intelligence = resultSet.getInt("Intelligenza");
                    int wisdom = resultSet.getInt("Saggezza");
                    int charisma = resultSet.getInt("Carisma");

                    if (resultSet.getInt("p.IDcreazione") != 0) {
                        // È un personaggio
                        String classType = resultSet.getString("Classe");
                        String race = resultSet.getString("Razza");
                        int level = resultSet.getInt("Livello");
                        Creation creation = new Creation(id, creationDate, name, description, strength, dexterity,
                                constitution, intelligence, wisdom, charisma);
                        result.add(new Character(creation, classType, race, level));
                    } else if (resultSet.getInt("m.IDcreazione") != 0) {
                        // È un mostro
                        String size = resultSet.getString("Taglia");
                        int challengeRating = resultSet.getInt("GradoSfida");
                        String type = resultSet.getString("Tipo");
                        Creation creation = new Creation(id, creationDate, name, description, strength, dexterity,
                                constitution, intelligence, wisdom, charisma);
                        result.add(new Monster(creation, size, challengeRating, type));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return result;
        }
    }

    public static void downloadCreation(Connection connection, int idInserzione, String username) {
        try {
            connection.setAutoCommit(false);

            // Inserimento nella tabella Download
            try (PreparedStatement downloadStatement = DAOUtils.prepare(connection, Queries.DOWNLOAD_CREATION,
                    idInserzione, username)) {
                downloadStatement.executeUpdate();
            }

            // Aggiornamento del numero di download nella tabella INSERZIONE
            try (PreparedStatement updateStatement = DAOUtils.prepare(connection, Queries.UPDATE_TOTAL_DOWNLOADS,
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
}