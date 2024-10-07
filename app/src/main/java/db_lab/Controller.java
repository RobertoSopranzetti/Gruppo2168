package db_lab;

import java.util.List;
import java.util.Objects;

import db_lab.data.CreationInterface;
import db_lab.data.User;
import db_lab.model.Model;

// The controller provides a holistic description of how the outside world can
// interact with our application: each public method is written as
//
//   subject + action + object (e.g. user + clicked + preview)
//
// So just by reading all the methods we know of all the possible interactions
// that can happen in our app. This makes it simpler to track all the possible
// actions that can take place as the application grows.
//
public final class Controller {

    // The controller holds a reference to the:
    // - model: to have it load new data
    // - view: to update it as new data is loaded
    //
    // ┌────── updates ──────┐
    // │ │
    // ┌──▼┐ ┌─┴────────┐ updates ┌──────┐
    // │view│ │controller├─────────►model│
    // └──┬─┘ └─▲───────┘ └──────┘
    // │ notifies │
    // └────── of user's ────┘
    // actions
    //
    private final Model model;
    private final View view;

    public Controller(Model model, View view) {
        Objects.requireNonNull(model, "Controller created with null model");
        Objects.requireNonNull(view, "Controller created with null view");
        this.view = view;
        this.model = model;
    }

    public void userClickedTopCreators() {
        List<User> topCreators = model.getTopCreators();
        view.showTopCreatorsPage(topCreators);
    }

    public void userClickedTopListings() {
        List<CreationInterface> topCreations = model.getTopListings();
        view.showTopDownloadsPage(topCreations);
    }

    public void userClickedBack() {
        String username = this.model.getCurrentUsername();
        if (username != null) {
            this.view.homePage(username);
        } else {
            this.loadInitialPage();
        }
    }

    public void userClickedRetry() {
        this.loadInitialPage();
    }

    public void adminClickedBack() {
        String username = this.model.getCurrentUsername();
        if (username != null) {
            this.view.adminPage(username);
        } else {
            this.loadInitialPage();
        }
    }

    void loadInitialPage() {
        this.view.loginPage();
    }

    public void userClickedUserSignIn() {
        view.showRegisterPage(false);
    }

    public void adminClickedAdminSignIn() {
        view.showRegisterPage(true);
    }

    public void userClickedUserLogin(String username, String password) {
        if (model.authenticate(username, password)) {
            model.setCurrentUsername(username);
            view.homePage(username);
        } else {
            view.showLoginError("Invalid username or password");
        }
    }

    public void adminClickedAdminLogin(String username, String password) {
        if (model.authenticateAdmin(username, password)) {
            model.setCurrentUsername(username);
            view.adminPage(username);
        } else {
            view.showLoginError("Invalid admin username or password");
        }
    }

    public void userAttemptedRegister(String username, String password, String confirmPassword, String firstName,
            String lastName, String email) {
        if (!password.equals(confirmPassword)) {
            view.showLoginError("Passwords do not match");
            return;
        }
        if (model.registerUser(username, password, firstName, lastName, email)) {
            model.setCurrentUsername(username);
            view.homePage(username);
        } else {
            view.showLoginError("Registration failed");
        }
    }

    public void adminAttemptedRegister(String username, String password, String confirmPassword, String firstName,
            String lastName, String email) {
        if (!password.equals(confirmPassword)) {
            view.showLoginError("Passwords do not match");
            return;
        }
        if (model.registerAdmin(username, password, firstName, lastName, email)) {
            model.setCurrentUsername(username);
            view.adminPage(username);
        } else {
            view.showLoginError("Registration failed");
        }
    }

    public void userClickedCreateCollection(String username) {
        view.showCreateCollectionPage(username);
    }

    public void userClickedCreateCollectionConfirm(String username, String collectionName, boolean isPrivate) {
        if (model.createCollection(username, collectionName, isPrivate)) {
            this.userClickedBack();
        } else {
            view.showUserError("Failed to create collection");
        }
    }

    public void userClickedCreateCreation(String username) {
        view.showInitialCreatePage(username);
    }

    public void userSelectedCreationType(int idCollection, boolean isMonster, String username) {
        view.showCreateCharacterOrMonsterPage(idCollection, isMonster, username);
    }

    public void userClickedCreateMonster(int idCollection, String name, String description, int strength, int dexterity,
            int constitution, int intelligence, int wisdom, int charisma, String size, int challengeRating,
            String type, boolean publishImmediately, String username) {
        int creationId = model.createMonster(name, description, strength, dexterity, constitution, intelligence,
                wisdom, charisma, idCollection, size, challengeRating, type);
        if (creationId > 0) {
            if (publishImmediately) {
                if (model.publishCreation(creationId, username)) {
                    this.userClickedBack();
                } else {
                    view.showUserError("Failed to publish monster");
                }
            } else {
                this.userClickedBack();
            }
        } else {
            view.showUserError("Failed to create monster");
        }
    }

    public void userClickedCreateCharacter(int idCollection, String name, String description, int strength,
            int dexterity, int constitution, int intelligence, int wisdom, int charisma, String classType, String race,
            int level, boolean publishImmediately, String username) {
        int creationId = model.createCharacter(name, description, strength, dexterity, constitution, intelligence,
                wisdom, charisma, idCollection, classType, race, level);
        if (creationId > 0) {
            if (publishImmediately) {
                if (model.publishCreation(creationId, username)) {
                    this.userClickedBack();
                } else {
                    view.showUserError("Failed to publish character");
                }
            } else {
                this.userClickedBack();
            }
        } else {
            view.showUserError("Failed to create character");
        }
    }

    public void adminClickedCreateSubcategory(String adminName) {
        view.showCreateSubcategoryPage(adminName);
    }

    public void adminClickedAssociateCreation() {
        view.showAssociateCreationPage();
    }

    public void adminClickedViewBadUsers() {
        List<String> badUsers = model.getBadUsers();
        view.showBadCreatorsPage(badUsers);
    }

    public void adminClickedViewReportedUsers(String adminName) {
        List<String> reportedUsers = model.getReportedUsers();
        view.showReportedCreatorsPage(reportedUsers, adminName);
    }

    public void adminClickedModerateUser(String adminName, String username, String reason) {
        if (model.moderateUser(adminName, username, reason)) {
            this.adminClickedBack();
        } else {
            view.showAdminError("Failed to moderate user " + username + ".");
        }
    }

    public List<Integer> getCollectionIdsByUsername(String username) {
        return model.getCollectionIdsByUsername(username);
    }

    public void adminClickedCreateSubcategoryConfirm(String adminName, String subcategoryName,
            String subcategoryDescription) {
        if (model.createSubcategory(adminName, subcategoryName, subcategoryDescription)) {
            this.adminClickedBack();
        } else {
            view.showAdminError("Failed to create subcategory '" + subcategoryName + "'.");
        }
    }

    public void adminClickedAssociateCreationConfirm(int creationId, int subcategoryId) {
        if (model.associateCreationToSubcategory(creationId, subcategoryId)) {
            this.adminClickedBack();
        } else {
            view.showAdminError(
                    "Failed to associate creation " + creationId + " with subcategory " + subcategoryId + ".");
        }
    }

    public List<Integer> getAllCreationIds() {
        return model.getAllCreationIds();
    }

    public List<Integer> getAllSubcategoryIds() {
        return model.getAllSubcategoryIds();
    }

    public void userNavigateVote(String username) {
        view.showVotePage(username);
    }

    public void userNavigateComment(String username) {
        view.showCommentPage(username);
    }

    public void userNavigateReportInsertion(String username) {
        view.showReportInsertionPage(username);
    }

    public void userNavigateReportComment(String username) {
        view.showReportCommentPage(username);
    }

    public void userNavigateDownload(String username) {
        view.showDownloadPage(username);
    }

    public void userClickedViewCategory() {
        view.showCategoryPage();
    }

    public void userClickedViewSubcategory() {
        view.showSubcategoryPage();
    }

    public void voteInsertion(Integer selectedId, String username, boolean b) {
        if (model.voteCreation(selectedId, username, b)) {
            this.userClickedBack();
        } else {
            view.showUserError("Failed to vote on insertion");
        }
    }

    public void commentInsertion(Integer selectedId, String username, String comment) {
        if (model.commentCreation(selectedId, username, comment)) {
            this.userClickedBack();
        } else {
            view.showUserError("Failed to comment on insertion");
        }
    }

    public void reportInsertion(Integer selectedId, String username, String reason) {
        if (model.reportInsertion(selectedId, username, reason)) {
            this.userClickedBack();
        } else {
            view.showUserError("Failed to report insertion");
        }
    }

    public List<Integer> getAllCommentIds() {
        return model.getAllCommentIds();
    }

    public void reportComment(Integer selectedId, String username, String reason) {
        if (model.reportComment(selectedId, username, reason)) {
            this.userClickedBack();
        } else {
            view.showUserError("Failed to report comment");
        }
    }

    public void downloadInsertion(Integer selectedId, String username) {
        if (model.downloadCreation(selectedId, username)) {
            this.userClickedBack();
        } else {
            view.showUserError("Failed to download insertion");
        }
    }

    public void handleViewCategory(Integer selectedId) {
        List<CreationInterface> category = model.getCategory(selectedId);
        view.showCategories(category);
    }

    public void handleViewSubcategory(Integer selectedId) {
        List<CreationInterface> subcategory = model.getSubcategory(selectedId);
        view.showSubcategories(subcategory);
    }

}
