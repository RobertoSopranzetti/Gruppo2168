package db_lab.model;

import java.sql.Connection;
import java.util.List;

import db_lab.data.CreationInterface;
import db_lab.data.User;

public interface Model {

    // Metodi di Autenticazione
    boolean authenticate(String username, String password);

    boolean authenticateAdmin(String username, String password);

    String getCurrentUsername();

    void setCurrentUsername(String username);

    // Metodi di Registrazione
    boolean registerUser(String username, String password, String firstName, String lastName, String email);

    boolean registerAdmin(String username, String password, String firstName, String lastName, String email);

    // Metodi di Moderazione
    List<String> getBadUsers();

    List<String> getReportedUsers();

    boolean moderateUser(String adminName, String username, String reason);

    // Metodi di Recupero
    List<User> getTopCreators();

    List<CreationInterface> getTopListings();

    List<Integer> getCollectionIdsByUsername(String username);

    List<Integer> getAllCreationIds();

    List<Integer> getAllSubcategoryIds();

    List<CreationInterface> getCategory(int idCreation);

    List<CreationInterface> getSubcategory(int inserzioneId);

    List<Integer> getAllCommentIds();

    // Metodi di Creazione
    boolean createCollection(String username, String collectionName, boolean isPrivate);

    int createMonster(String name, String description, int strength, int dexterity,
            int constitution, int intelligence, int wisdom, int charisma, int idCollection, String size,
            int challengeRating, String type);

    int createCharacter(String name, String description, int strength, int dexterity,
            int constitution, int intelligence, int wisdom, int charisma, int idCollection, String classType,
            String race, int level);

    boolean createSubcategory(String adminName, String subcategoryName, String subcategoryDescription);

    boolean publishCreation(int creationId, String username);

    // Metodi di Votazione, Commento e Segnalazione

    boolean voteCreation(int idInserzione, String username, boolean tipo);

    boolean downloadCreation(int idInserzione, String username);

    boolean commentCreation(int idInserzione, String username, String commento);

    boolean reportInsertion(int idInserzione, String username, String motivo);

    boolean reportComment(int idCommento, String username, String motivo);

    // Metodi di Associazione
    boolean associateCreationToSubcategory(int creationId, int subcategoryId);

    // Creazione del modello da una connessione al database
    static Model fromConnection(Connection connection) {
        return new DBModel(connection);
    }

}