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

// This is the real model implementation that uses the DAOs we've defined to
// actually load data from the underlying database.
//
// As you can see this model doesn't do too much except loading data from the
// database and keeping a cache of the loaded previews.
// A real model might be doing much more, but for the sake of the example we're
// keeping it simple.
//
public final class DBModel implements Model {

    private final Connection connection;
    private String currentUsername;

    public DBModel(Connection connection) {
        Objects.requireNonNull(connection, "Model created with null connection");
        this.connection = connection;
    }

    @Override
    public boolean authenticate(String username, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'authenticate'");
    }

    @Override
    public boolean authenticateAdmin(String username, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'authenticateAdmin'");
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
                SELECT IDcreazione
                FROM Creazioni
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
                SELECT IDsubcategoria
                FROM Subcategorie
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
    public boolean createCollection(String username, String collectionName, boolean isPrivate) {
        try {
            User.DAO.createCollection(connection, username, isPrivate, collectionName);
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

}
