package db_lab.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import db_lab.data.Admin;
import db_lab.data.Creation;
import db_lab.data.CreationInterface;
import db_lab.data.DAOException;
import db_lab.data.DAOUtils;
import db_lab.data.User;

public final class DBModel implements Model {

    private final Connection connection;
    private String currentUsername;

    public DBModel(Connection connection) {
        Objects.requireNonNull(connection, "Model created with null connection");
        this.connection = connection;
    }

    @Override
    public boolean authenticate(String username, String password) {
        String query = "SELECT COUNT(*) FROM UTENTE WHERE Username = ? AND Password = ?";
        try (PreparedStatement statement = DAOUtils.prepare(connection, query, username, password);
                ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean authenticateAdmin(String username, String password) {
        String query = "SELECT COUNT(*) FROM ADMIN WHERE Username = ? AND Password = ?";
        try (PreparedStatement statement = DAOUtils.prepare(connection, query, username, password);
                ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getCurrentUsername() {
        return this.currentUsername;
    }

    @Override
    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    @Override
    public boolean registerUser(String username, String password, String firstName, String lastName, String email) {
        try {
            User.DAO.signUpUser(connection, username, password, firstName, lastName, email);
            return true;
        } catch (Exception e) {
            throw new DAOException("Failed to register user", e);
        }
    }

    @Override
    public boolean registerAdmin(String username, String password, String firstName, String lastName, String email) {
        try {
            Admin.DAO.signUpAdmin(connection, username, password, firstName, lastName, email);
            return true;
        } catch (Exception e) {
            throw new DAOException("Failed to register user", e);
        }
    }

    @Override
    public List<String> getBadUsers() {
        return Admin.DAO.badUsers(connection);
    }

    @Override
    public List<String> getReportedUsers() {
        return Admin.DAO.reportedUsers(connection);
    }

    @Override
    public boolean moderateUser(String adminName, String username, String reason) {
        try {
            Admin.DAO.userModeration(connection, adminName, username, reason);
            return true;
        } catch (Exception e) {
            throw new DAOException("Failed to moderate user", e);
        }
    }

    @Override
    public List<User> getTopCreators() {
        return User.DAO.topCreators(connection);
    }

    @Override
    public List<CreationInterface> getTopListings() {
        return Creation.DAO.topCreations(connection);
    }

    @Override
    public List<Integer> getCollectionIdsByUsername(String username) {
        return User.DAO.getCollectionIdsByUsername(connection, username);
    }

    @Override
    public List<Integer> getAllCreationIds() {
        List<Integer> creationIds = new ArrayList<>();
        String query = """
                SELECT IDinserzione
                FROM INSERZIONE
                """;

        try (PreparedStatement statement = DAOUtils.prepare(connection, query);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                creationIds.add(resultSet.getInt("IDcreazione"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return creationIds;
    }

    @Override
    public List<Integer> getAllSubcategoryIds() {
        List<Integer> subcategoryIds = new ArrayList<>();
        String query = """
                SELECT IDsottocategoria
                FROM SOTTOCATEGORIA
                """;

        try (PreparedStatement statement = DAOUtils.prepare(connection, query);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                subcategoryIds.add(resultSet.getInt("IDsubcategoria"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subcategoryIds;
    }

    @Override
    public List<Integer> getAllCommentIds() {
        List<Integer> commentIds = new ArrayList<>();
        String query = """
                SELECT IDcommento
                FROM Commenti
                """;

        try (PreparedStatement statement = DAOUtils.prepare(connection, query);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                commentIds.add(resultSet.getInt("IDcommento"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commentIds;
    }

    @Override
    public boolean createCollection(String username, String collectionName, boolean isPrivate) {
        try {
            User.DAO.createCollection(connection, collectionName, isPrivate, username);
            return true;
        } catch (Exception e) {
            throw new DAOException("Failed to create collection", e);
        }
    }

    @Override
    public int createMonster(String name, String description, int strength, int dexterity,
            int constitution, int intelligence, int wisdom, int charisma, int idCollection, String size,
            int challengeRating, String type) {

        return User.DAO.createMonster(connection, name, description, strength, dexterity, constitution,
                intelligence, wisdom, charisma, idCollection, size, challengeRating, type);

    }

    @Override
    public int createCharacter(String name, String description, int strength, int dexterity,
            int constitution, int intelligence, int wisdom, int charisma, int idCollection, String classType,
            String race, int level) {

        return User.DAO.createCharacter(connection, name, description, strength, dexterity, constitution, intelligence,
                wisdom, charisma, idCollection, classType, race, level);

    }

    @Override
    public boolean publishCreation(int creationId, String username) {
        try {
            Creation.DAO.publishCreation(connection, creationId, username);
            return true;
        } catch (Exception e) {
            throw new DAOException("Failed to publish creation", e);
        }
    }

    @Override
    public boolean voteCreation(int idInserzione, String username, boolean tipo) {
        try {
            User.DAO.voteCreation(connection, idInserzione, username, tipo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean commentCreation(int idInserzione, String username, String commento) {
        try {
            User.DAO.commentCreation(connection, idInserzione, username, commento);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean reportInsertion(int idInserzione, String username, String motivo) {
        try {
            User.DAO.reportInsertion(connection, idInserzione, username, motivo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean reportComment(int idCommento, String username, String motivo) {
        try {
            User.DAO.reportComment(connection, idCommento, username, motivo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createSubcategory(String adminName, String subcategoryName, String subcategoryDescription) {
        try {
            Admin.DAO.addSubcategory(connection, subcategoryName, subcategoryDescription, adminName);
            return true;
        } catch (Exception e) {
            throw new DAOException("Failed to create subcategory", e);
        }
    }

    @Override
    public boolean associateCreationToSubcategory(int creationId, int subcategoryId) {
        try {
            Admin.DAO.addToSubcategory(connection, creationId, subcategoryId);
            return true;
        } catch (Exception e) {
            throw new DAOException("Failed to associate creation to subcategory", e);
        }
    }

    @Override
    public List<CreationInterface> getCategory(int idCreation) {
        return Creation.DAO.getCategory(connection, idCreation);
    }

    @Override
    public List<CreationInterface> getSubcategory(int inserzioneId) {
        return Creation.DAO.getSubcategory(connection, inserzioneId);
    }

    @Override
    public boolean downloadCreation(int idInserzione, String username) {
        try {
            Creation.DAO.downloadCreation(connection, idInserzione, username);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
