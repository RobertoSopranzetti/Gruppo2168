package db_lab;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import db_lab.data.Character;
import db_lab.data.CreationInterface;
import db_lab.data.Monster;
import db_lab.data.User;

public final class View {

    private Optional<Controller> controller;
    private final JFrame mainFrame;

    // We take an action to run before closing the view so that one can gracefully
    // deal with open resources.
    public View(Runnable onClose) {
        this.controller = Optional.empty();
        this.mainFrame = this.setupMainFrame(onClose);
    }

    private JFrame setupMainFrame(Runnable onClose) {
        var frame = new JFrame("BeastTavern");
        var padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        ((JComponent) frame.getContentPane()).setBorder(padding);
        frame.setMinimumSize(new Dimension(300, 100));
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        frame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        onClose.run();
                        System.exit(0);
                    }
                });

        return frame;
    }

    private Controller getController() {
        if (this.controller.isPresent()) {
            return this.controller.get();
        } else {
            throw new IllegalStateException(
                    """
                            The View's Controller is undefined, did you remember to call
                            `setController` before starting the application?
                            Remeber that `View` needs a reference to the controller in order
                            to notify it of button clicks and other changes.
                            """);
        }
    }

    public void setController(Controller controller) {
        Objects.requireNonNull(controller, "Set null controller in view");
        this.controller = Optional.of(controller);
    }

    public void loginPage() {
        freshPane(cp -> {
            if (!(cp instanceof JPanel)) {
                throw new IllegalArgumentException("Expected cp to be an instance of JPanel");
            }
            JPanel panel = (JPanel) cp;
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            panel.add(Box.createVerticalGlue());
            panel.add(new JLabel("Login Page", SwingConstants.CENTER));
            panel.add(Box.createVerticalStrut(10));

            // Login come utente
            JTextField userLoginUsernameField = new JTextField(20);
            JPasswordField userLoginPasswordField = new JPasswordField(20);
            addLabeledTextField(panel, "Username:", userLoginUsernameField);
            addLabeledTextField(panel, "Password:", userLoginPasswordField);

            JButton userLoginButton = button("Login come Utente", () -> this.getController().userClickedUserLogin(
                    userLoginUsernameField.getText(), new String(userLoginPasswordField.getPassword())));
            panel.add(userLoginButton);

            panel.add(Box.createVerticalStrut(10));

            // Sign in come utente (testo cliccabile)
            JLabel userSignInLabel = clickableLabel("Sign In come Utente",
                    () -> this.getController().userClickedUserSignIn());
            panel.add(userSignInLabel);

            panel.add(Box.createVerticalStrut(10));

            // Login come admin
            JTextField adminLoginUsernameField = new JTextField(20);
            JPasswordField adminLoginPasswordField = new JPasswordField(20);
            addLabeledTextField(panel, "Admin Name:", adminLoginUsernameField);
            addLabeledTextField(panel, "Password:", adminLoginPasswordField);

            JButton adminLoginButton = button("Login come Admin", () -> this.getController().adminClickedAdminLogin(
                    adminLoginUsernameField.getText(), new String(adminLoginPasswordField.getPassword())));
            panel.add(adminLoginButton);

            panel.add(Box.createVerticalStrut(10));

            // Sign in come admin (testo cliccabile)
            JLabel adminSignInLabel = clickableLabel("Sign In come Admin",
                    () -> this.getController().adminClickedAdminSignIn());
            panel.add(adminSignInLabel);

            panel.add(Box.createVerticalGlue());
        });
    }

    public void showLoginError(String message) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            cp.add(Box.createVerticalGlue());
            cp.add(new JLabel(message, SwingConstants.CENTER));
            cp.add(Box.createVerticalStrut(10));

            JButton retryButton = button("Retry", () -> this.getController().userClickedRetry());
            cp.add(retryButton);

            cp.add(Box.createVerticalGlue());
        });
    }

    public void showRegisterPage(boolean isAdmin) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();
            JPasswordField confirmPasswordField = new JPasswordField();
            JTextField firstNameField = new JTextField();
            JTextField lastNameField = new JTextField();
            JTextField emailField = new JTextField();

            // Assicurati che cp sia di tipo JPanel
            if (cp instanceof JPanel) {
                JPanel panel = (JPanel) cp;
                addLabeledTextField(panel, isAdmin ? "Admin Name:" : "Username:", usernameField);
                addLabeledTextField(panel, "Password:", passwordField);
                addLabeledTextField(panel, "Confirm Password:", confirmPasswordField);
                addLabeledTextField(panel, "Nome:", firstNameField);
                addLabeledTextField(panel, "Cognome:", lastNameField);
                addLabeledTextField(panel, "Email:", emailField);

                panel.add(Box.createVerticalStrut(10));
                JButton registerButton = button("Register", () -> {
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());
                    String confirmPassword = new String(confirmPasswordField.getPassword());
                    String firstName = firstNameField.getText();
                    String lastName = lastNameField.getText();
                    String email = emailField.getText();
                    if (isAdmin) {
                        this.getController().adminAttemptedRegister(username, password, confirmPassword, firstName,
                                lastName, email);
                    } else {
                        this.getController().userAttemptedRegister(username, password, confirmPassword, firstName,
                                lastName, email);
                    }
                });
                panel.add(registerButton);

                panel.add(Box.createVerticalStrut(10));
                JButton backButton = button("Back", () -> this.getController().userClickedBack());
                panel.add(backButton);

                panel.add(Box.createVerticalGlue());
            } else {
                throw new IllegalArgumentException("Expected cp to be an instance of JPanel");
            }
        });
    }

    public void homePage(String username) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            cp.add(Box.createVerticalGlue());
            cp.add(new JLabel("BENVENUTO " + username, SwingConstants.CENTER));
            cp.add(Box.createVerticalStrut(10));

            JButton topCreatorsButton = button("Top Creatori", () -> this.getController().userClickedTopCreators());
            cp.add(topCreatorsButton);

            cp.add(Box.createVerticalStrut(10));

            JButton topListingsButton = button("Top Inserzioni", () -> this.getController().userClickedTopListings());
            cp.add(topListingsButton);

            cp.add(Box.createVerticalStrut(10));

            JButton createCollectionButton = button("Crea Raccolta",
                    () -> this.getController().userClickedCreateCollection(username));
            cp.add(createCollectionButton);

            cp.add(Box.createVerticalStrut(10));

            JButton createCreationButton = button("Crea Creazione",
                    () -> this.getController().userClickedCreateCreation(username));
            cp.add(createCreationButton);

            cp.add(Box.createVerticalStrut(10));

            // Aggiungi le label cliccabili
            JLabel voteLabel = clickableLabel("Vota", () -> this.getController().userNavigateVote(username));
            cp.add(voteLabel);

            cp.add(Box.createVerticalStrut(10));

            JLabel commentLabel = clickableLabel("Commenta", () -> this.getController().userNavigateComment(username));
            cp.add(commentLabel);

            cp.add(Box.createVerticalStrut(10));

            JLabel reportInsertionLabel = clickableLabel("Segnala Inserzione",
                    () -> this.getController().userNavigateReportInsertion(username));
            cp.add(reportInsertionLabel);

            cp.add(Box.createVerticalStrut(10));

            JLabel reportCommentLabel = clickableLabel("Segnala Commento",
                    () -> this.getController().userNavigateReportComment(username));
            cp.add(reportCommentLabel);

            cp.add(Box.createVerticalStrut(10));

            JLabel downloadLabel = clickableLabel("Scarica", () -> this.getController().userNavigateDownload(username));
            cp.add(downloadLabel);

            cp.add(Box.createVerticalStrut(10));

            JLabel categoryLabel = clickableLabel("Vedi Categoria",
                    () -> this.getController().userClickedViewCategory());
            cp.add(categoryLabel);

            cp.add(Box.createVerticalStrut(10));

            JLabel subcategoryLabel = clickableLabel("Vedi Sottocategoria",
                    () -> this.getController().userClickedViewSubcategory());
            cp.add(subcategoryLabel);

            cp.add(Box.createVerticalStrut(10));

            JButton logoutButton = button("Esci", () -> this.getController().loadInitialPage());
            cp.add(logoutButton);

            cp.add(Box.createVerticalGlue());
        });
    }

    public void showCreateCollectionPage(String username) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            JTextField collectionNameField = new JTextField();
            JCheckBox privateCheckBox = new JCheckBox("Privata");

            // Assicurati che cp sia di tipo JPanel
            if (cp instanceof JPanel) {
                JPanel panel = (JPanel) cp;
                panel.add(new JLabel("Crea Raccolta per " + username, SwingConstants.CENTER));
                panel.add(Box.createVerticalStrut(10));

                addLabeledTextField(panel, "Nome Raccolta:", collectionNameField);

                panel.add(privateCheckBox);

                panel.add(Box.createVerticalStrut(10));
                JButton createCollectionButton = button("Crea Raccolta", () -> {
                    String collectionName = collectionNameField.getText();
                    boolean isPrivate = privateCheckBox.isSelected();
                    this.getController().userClickedCreateCollectionConfirm(username, collectionName, isPrivate);
                });
                panel.add(createCollectionButton);

                panel.add(Box.createVerticalStrut(10));
                JButton backButton = button("Back", () -> this.getController().userClickedBack());
                panel.add(backButton);

                panel.add(Box.createVerticalGlue());
            } else {
                throw new IllegalArgumentException("Expected cp to be an instance of JPanel");
            }
        });
    }

    public void showInitialCreatePage(String username) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            JComboBox<Integer> idCollectionComboBox = new JComboBox<>();

            // Recupera gli ID delle raccolte dal database
            List<Integer> collectionIds = this.getController().getCollectionIdsByUsername(username);
            for (Integer id : collectionIds) {
                idCollectionComboBox.addItem(id);
            }

            // Assicurati che cp sia di tipo JPanel
            if (cp instanceof JPanel) {
                JPanel panel = (JPanel) cp;
                panel.add(new JLabel("Seleziona l'ID della raccolta e il tipo di creazione", SwingConstants.CENTER));
                panel.add(Box.createVerticalStrut(10));

                panel.add(new JLabel("ID Raccolta:"));
                panel.add(idCollectionComboBox);

                panel.add(Box.createVerticalStrut(10));
                JButton characterButton = button("Crea Personaggio", () -> {
                    int idCollection = (int) idCollectionComboBox.getSelectedItem();
                    this.getController().userSelectedCreationType(idCollection, false, username);
                });
                panel.add(characterButton);

                panel.add(Box.createVerticalStrut(10));
                JButton monsterButton = button("Crea Mostro", () -> {
                    int idCollection = (int) idCollectionComboBox.getSelectedItem();
                    this.getController().userSelectedCreationType(idCollection, true, username);
                });
                panel.add(monsterButton);

                panel.add(Box.createVerticalStrut(10));
                JButton backButton = button("Back", () -> this.getController().userClickedBack());
                panel.add(backButton);

                panel.add(Box.createVerticalGlue());
            } else {
                throw new IllegalArgumentException("Expected cp to be an instance of JPanel");
            }
        });
    }

    public void showCreateCharacterOrMonsterPage(int idCollection, boolean isMonster, String username) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            JTextField nameField = new JTextField();
            JTextField descriptionField = new JTextField();
            JComboBox<Integer> strengthComboBox = new JComboBox<>(createIntegerArray(1, 20));
            JComboBox<Integer> dexterityComboBox = new JComboBox<>(createIntegerArray(1, 20));
            JComboBox<Integer> constitutionComboBox = new JComboBox<>(createIntegerArray(1, 20));
            JComboBox<Integer> intelligenceComboBox = new JComboBox<>(createIntegerArray(1, 20));
            JComboBox<Integer> wisdomComboBox = new JComboBox<>(createIntegerArray(1, 20));
            JComboBox<Integer> charismaComboBox = new JComboBox<>(createIntegerArray(1, 20));

            JTextField classTypeField = new JTextField();
            JTextField raceField = new JTextField();
            JTextField levelField = new JTextField();

            JTextField sizeField = new JTextField();
            JTextField challengeRatingField = new JTextField();
            JTextField monsterTypeField = new JTextField();

            JCheckBox publishCheckBox = new JCheckBox();

            // Assicurati che cp sia di tipo JPanel
            if (cp instanceof JPanel) {
                JPanel panel = (JPanel) cp;
                panel.add(new JLabel(
                        "Crea " + (isMonster ? "Mostro" : "Personaggio") + " per la Raccolta " + idCollection,
                        SwingConstants.CENTER));
                panel.add(Box.createVerticalStrut(10));

                addLabeledTextField(panel, "Nome:", nameField);
                addLabeledTextField(panel, "Descrizione:", descriptionField);
                addLabeledComboBox(panel, "Forza:", strengthComboBox);
                addLabeledComboBox(panel, "Destrezza:", dexterityComboBox);
                addLabeledComboBox(panel, "Costituzione:", constitutionComboBox);
                addLabeledComboBox(panel, "Intelligenza:", intelligenceComboBox);
                addLabeledComboBox(panel, "Saggezza:", wisdomComboBox);
                addLabeledComboBox(panel, "Carisma:", charismaComboBox);

                if (isMonster) {
                    addLabeledTextField(panel, "Dimensione:", sizeField);
                    addLabeledTextField(panel, "Grado di Sfida:", challengeRatingField);
                    addLabeledTextField(panel, "Tipo:", monsterTypeField);
                } else {
                    addLabeledTextField(panel, "Classe:", classTypeField);
                    addLabeledTextField(panel, "Razza:", raceField);
                    addLabeledTextField(panel, "Livello:", levelField);
                }

                panel.add(Box.createVerticalStrut(10));
                panel.add(publishCheckBox);

                panel.add(Box.createVerticalStrut(10));
                JButton createButton = button("Crea " + (isMonster ? "Mostro" : "Personaggio"), () -> {
                    String name = nameField.getText();
                    String description = descriptionField.getText();
                    int strength = (int) strengthComboBox.getSelectedItem();
                    int dexterity = (int) dexterityComboBox.getSelectedItem();
                    int constitution = (int) constitutionComboBox.getSelectedItem();
                    int intelligence = (int) intelligenceComboBox.getSelectedItem();
                    int wisdom = (int) wisdomComboBox.getSelectedItem();
                    int charisma = (int) charismaComboBox.getSelectedItem();

                    boolean publishImmediately = publishCheckBox.isSelected();

                    if (isMonster) {
                        String size = sizeField.getText();
                        int challengeRating = Integer.parseInt(challengeRatingField.getText());
                        String type = monsterTypeField.getText();
                        this.getController().userClickedCreateMonster(idCollection, name, description, strength,
                                dexterity, constitution, intelligence, wisdom, charisma, size, challengeRating, type,
                                publishImmediately, username);
                    } else {
                        String classType = classTypeField.getText();
                        String race = raceField.getText();
                        int level = Integer.parseInt(levelField.getText());
                        this.getController().userClickedCreateCharacter(idCollection, name, description, strength,
                                dexterity, constitution, intelligence, wisdom, charisma, classType, race, level,
                                publishImmediately, username);
                    }
                });
                panel.add(createButton);

                panel.add(Box.createVerticalStrut(10));
                JButton backButton = button("Back", () -> this.getController().userClickedBack());
                panel.add(backButton);

                panel.add(Box.createVerticalGlue());
            } else {
                throw new IllegalArgumentException("Expected cp to be an instance of JPanel");
            }
        });
    }

    public void adminPage(String adminName) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            cp.add(Box.createVerticalGlue());
            cp.add(new JLabel("BENVENUTO ADMIN " + adminName, SwingConstants.CENTER));
            cp.add(Box.createVerticalStrut(10));

            JLabel createSubcategoryLabel = clickableLabel("Crea Sottocategoria",
                    () -> this.getController().adminClickedCreateSubcategory(adminName));
            cp.add(createSubcategoryLabel);

            cp.add(Box.createVerticalStrut(10));

            JLabel associateCreationLabel = clickableLabel("Associa Creazione a Sottocategoria",
                    () -> this.getController().adminClickedAssociateCreation());
            cp.add(associateCreationLabel);

            cp.add(Box.createVerticalStrut(10));

            JLabel viewBadUsersLabel = clickableLabel("Visualizza Utenti con 1000 downvotes",
                    () -> this.getController().adminClickedViewBadUsers());
            cp.add(viewBadUsersLabel);

            cp.add(Box.createVerticalStrut(10));

            JLabel viewReportedUsersLabel = clickableLabel("Visualizza Utenti Segnalati",
                    () -> this.getController().adminClickedViewReportedUsers(adminName));
            cp.add(viewReportedUsersLabel);

            cp.add(Box.createVerticalStrut(10));

            JButton logoutButton = button("Esci", () -> this.getController().loadInitialPage());
            cp.add(logoutButton);

            cp.add(Box.createVerticalGlue());
        });
    }

    public void showCreateSubcategoryPage(String adminName) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            JTextField subcategoryNameField = new JTextField();
            JTextField subcategoryDescriptionField = new JTextField();

            // Assicurati che cp sia di tipo JPanel
            if (cp instanceof JPanel) {
                JPanel panel = (JPanel) cp;
                panel.add(new JLabel("Crea Sottocategoria per " + adminName, SwingConstants.CENTER));
                panel.add(Box.createVerticalStrut(10));

                addLabeledTextField(panel, "Nome Sottocategoria:", subcategoryNameField);
                addLabeledTextField(panel, "Descrizione Sottocategoria:", subcategoryDescriptionField);

                panel.add(Box.createVerticalStrut(10));
                JButton createSubcategoryButton = button("Crea Sottocategoria", () -> {
                    String subcategoryName = subcategoryNameField.getText();
                    String subcategoryDescription = subcategoryDescriptionField.getText();
                    this.getController().adminClickedCreateSubcategoryConfirm(adminName, subcategoryName,
                            subcategoryDescription);
                });
                panel.add(createSubcategoryButton);

                panel.add(Box.createVerticalStrut(10));
                JButton backButton = button("Back", () -> this.getController().userClickedBack());
                panel.add(backButton);

                panel.add(Box.createVerticalGlue());
            } else {
                throw new IllegalArgumentException("Expected cp to be an instance of JPanel");
            }
        });
    }

    public void showAssociateCreationPage() {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            JComboBox<Integer> creationIdComboBox = new JComboBox<>();
            JComboBox<Integer> subcategoryIdComboBox = new JComboBox<>();

            // Recupera gli ID delle creazioni e delle sottocategorie dal database
            List<Integer> creationIds = this.getController().getAllCreationIds();
            List<Integer> subcategoryIds = this.getController().getAllSubcategoryIds();

            for (Integer id : creationIds) {
                creationIdComboBox.addItem(id);
            }

            for (Integer id : subcategoryIds) {
                subcategoryIdComboBox.addItem(id);
            }

            // Assicurati che cp sia di tipo JPanel
            if (cp instanceof JPanel) {
                JPanel panel = (JPanel) cp;
                panel.add(new JLabel("Associa Creazione a Sottocategoria", SwingConstants.CENTER));
                panel.add(Box.createVerticalStrut(10));

                panel.add(new JLabel("ID Creazione:"));
                panel.add(creationIdComboBox);

                panel.add(Box.createVerticalStrut(10));

                panel.add(new JLabel("ID Sottocategoria:"));
                panel.add(subcategoryIdComboBox);

                panel.add(Box.createVerticalStrut(10));
                JButton associateButton = button("Associa", () -> {
                    int creationId = (int) creationIdComboBox.getSelectedItem();
                    int subcategoryId = (int) subcategoryIdComboBox.getSelectedItem();
                    this.getController().adminClickedAssociateCreationConfirm(creationId, subcategoryId);
                });
                panel.add(associateButton);

                panel.add(Box.createVerticalStrut(10));
                JButton backButton = button("Back", () -> this.getController().adminClickedBack());
                panel.add(backButton);

                panel.add(Box.createVerticalGlue());
            } else {
                throw new IllegalArgumentException("Expected cp to be an instance of JPanel");
            }
        });
    }

    public void showTopCreatorsPage(List<User> topCreators) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            cp.add(new JLabel("Top Creatori:", SwingConstants.CENTER));
            cp.add(Box.createVerticalStrut(10)); // Spazio tra i campi

            int rank = 1;
            for (User creator : topCreators) {
                String creatorInfo = rank + ". " + creator.username() + " " + creator.totalVotes();
                cp.add(new JLabel(creatorInfo));
                cp.add(Box.createVerticalStrut(5)); // Spazio tra i creatori
                rank++;
            }

            cp.add(Box.createVerticalStrut(20)); // Spazio prima del pulsante
            JButton backButton = button("Back", () -> this.getController().userClickedBack());
            cp.add(backButton);

            cp.add(Box.createVerticalGlue()); // Spazio flessibile alla fine
        });
    }

    public void showBadCreatorsPage(List<String> badCreators) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            cp.add(new JLabel("Bad Creatori:", SwingConstants.CENTER));
            cp.add(Box.createVerticalStrut(10)); // Spazio tra i campi

            for (String creator : badCreators) {
                cp.add(new JLabel(creator));
                cp.add(Box.createVerticalStrut(5)); // Spazio tra i creatori
            }

            cp.add(Box.createVerticalStrut(20)); // Spazio prima del pulsante
            JButton backButton = button("Back", () -> this.getController().adminClickedBack());
            cp.add(backButton);

            cp.add(Box.createVerticalGlue()); // Spazio flessibile alla fine
        });
    }

    public void showReportedCreatorsPage(List<String> reportedCreators, String adminName) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            cp.add(new JLabel("Creatori Segnalati:", SwingConstants.CENTER));
            cp.add(Box.createVerticalStrut(10)); // Spazio tra i campi

            for (String creator : reportedCreators) {
                JPanel creatorPanel = new JPanel();
                creatorPanel.setLayout(new BoxLayout(creatorPanel, BoxLayout.X_AXIS));
                creatorPanel.add(new JLabel(creator));
                creatorPanel.add(Box.createHorizontalStrut(10)); // Spazio tra il nome e il pulsante

                JButton moderateButton = button("Modera", () -> showModerationDialog(creator, adminName));
                creatorPanel.add(moderateButton);

                cp.add(creatorPanel);
                cp.add(Box.createVerticalStrut(5)); // Spazio tra i creatori
            }

            cp.add(Box.createVerticalStrut(20)); // Spazio prima del pulsante
            JButton backButton = button("Back", () -> this.getController().adminClickedBack());
            cp.add(backButton);

            cp.add(Box.createVerticalGlue()); // Spazio flessibile alla fine
        });
    }

    private void showModerationDialog(String username, String adminName) {
        JTextField reasonField = new JTextField();
        Object[] message = {
                "Motivazione:", reasonField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Inserisci Motivazione per Moderazione",
                JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String reason = reasonField.getText();
            if (!reason.trim().isEmpty()) {
                this.getController().adminClickedModerateUser(adminName, username, reason);
            } else {
                JOptionPane.showMessageDialog(null, "La motivazione non può essere vuota.");
            }
        }
    }

    public void showTopDownloadsPage(List<CreationInterface> topDownloads) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            // Creazione del pannello principale
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

            mainPanel.add(new JLabel("Top Creazioni Scaricate:", SwingConstants.CENTER));
            mainPanel.add(Box.createVerticalStrut(10)); // Spazio tra i campi

            int rank = 1;
            for (CreationInterface creation : topDownloads) {
                String creationInfo = String.format("%d. %s", rank, creation.name());
                mainPanel.add(new JLabel(creationInfo));
                mainPanel.add(new JLabel("Descrizione: " + creation.description()));
                mainPanel.add(new JLabel("Forza: " + creation.strength()));
                mainPanel.add(new JLabel("Destrezza: " + creation.dexterity()));
                mainPanel.add(new JLabel("Costituzione: " + creation.constitution()));
                mainPanel.add(new JLabel("Intelligenza: " + creation.intelligence()));
                mainPanel.add(new JLabel("Saggezza: " + creation.wisdom()));
                mainPanel.add(new JLabel("Carisma: " + creation.charisma()));

                if (creation instanceof Character character) {
                    mainPanel.add(new JLabel("Classe: " + character.classType()));
                    mainPanel.add(new JLabel("Razza: " + character.race()));
                    mainPanel.add(new JLabel("Livello: " + character.level()));
                } else if (creation instanceof Monster monster) {
                    mainPanel.add(new JLabel("Taglia: " + monster.size()));
                    mainPanel.add(new JLabel("Grado di Sfida: " + monster.challengeRating()));
                    mainPanel.add(new JLabel("Tipo: " + monster.type()));
                }

                mainPanel.add(Box.createVerticalStrut(10)); // Spazio tra le creazioni
                rank++;
            }

            mainPanel.add(Box.createVerticalStrut(20)); // Spazio prima del pulsante
            JButton backButton = button("Back", () -> this.getController().userClickedBack());
            mainPanel.add(backButton);

            // Creazione di un JScrollPane per rendere il pannello scorrevole
            JScrollPane scrollPane = new JScrollPane(mainPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            // Aggiunta dello JScrollPane al contenitore principale
            cp.add(scrollPane);
        });
    }

    public void showCategories(List<CreationInterface> categories) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
    
            // Creazione del pannello principale
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    
            mainPanel.add(new JLabel("Categorie:", SwingConstants.CENTER));
            mainPanel.add(Box.createVerticalStrut(10)); // Spazio tra i campi
    
            for (CreationInterface creation : categories) {
                mainPanel.add(new JLabel("Nome: " + creation.name()));
                mainPanel.add(new JLabel("Descrizione: " + creation.description()));
                mainPanel.add(new JLabel("Forza: " + creation.strength()));
                mainPanel.add(new JLabel("Destrezza: " + creation.dexterity()));
                mainPanel.add(new JLabel("Costituzione: " + creation.constitution()));
                mainPanel.add(new JLabel("Intelligenza: " + creation.intelligence()));
                mainPanel.add(new JLabel("Saggezza: " + creation.wisdom()));
                mainPanel.add(new JLabel("Carisma: " + creation.charisma()));

                if (creation instanceof Character character) {
                    mainPanel.add(new JLabel("Classe: " + character.classType()));
                    mainPanel.add(new JLabel("Razza: " + character.race()));
                    mainPanel.add(new JLabel("Livello: " + character.level()));
                } else if (creation instanceof Monster monster) {
                    mainPanel.add(new JLabel("Taglia: " + monster.size()));
                    mainPanel.add(new JLabel("Grado di Sfida: " + monster.challengeRating()));
                    mainPanel.add(new JLabel("Tipo: " + monster.type()));
                }

                mainPanel.add(Box.createVerticalStrut(10)); // Spazio tra le creazioni
            }
    
            mainPanel.add(Box.createVerticalStrut(20)); // Spazio prima del pulsante
            JButton backButton = button("Back", () -> this.getController().userClickedBack());
            mainPanel.add(backButton);
    
            // Creazione di un JScrollPane per rendere il pannello scorrevole
            JScrollPane scrollPane = new JScrollPane(mainPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
            // Aggiunta dello JScrollPane al contenitore principale
            cp.add(scrollPane);
        });
    }

    public void showSubcategories(List<CreationInterface> subcategories) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
    
            // Creazione del pannello principale
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    
            mainPanel.add(new JLabel("Sottocategorie:", SwingConstants.CENTER));
            mainPanel.add(Box.createVerticalStrut(10)); // Spazio tra i campi
    
            for (CreationInterface creation : subcategories) {
                mainPanel.add(new JLabel("Nome: " + creation.name()));
                mainPanel.add(new JLabel("Descrizione: " + creation.description()));
                mainPanel.add(new JLabel("Forza: " + creation.strength()));
                mainPanel.add(new JLabel("Destrezza: " + creation.dexterity()));
                mainPanel.add(new JLabel("Costituzione: " + creation.constitution()));
                mainPanel.add(new JLabel("Intelligenza: " + creation.intelligence()));
                mainPanel.add(new JLabel("Saggezza: " + creation.wisdom()));
                mainPanel.add(new JLabel("Carisma: " + creation.charisma()));

                if (creation instanceof Character character) {
                    mainPanel.add(new JLabel("Classe: " + character.classType()));
                    mainPanel.add(new JLabel("Razza: " + character.race()));
                    mainPanel.add(new JLabel("Livello: " + character.level()));
                } else if (creation instanceof Monster monster) {
                    mainPanel.add(new JLabel("Taglia: " + monster.size()));
                    mainPanel.add(new JLabel("Grado di Sfida: " + monster.challengeRating()));
                    mainPanel.add(new JLabel("Tipo: " + monster.type()));
                }

                mainPanel.add(Box.createVerticalStrut(10)); // Spazio tra le creazioni
            }
    
            mainPanel.add(Box.createVerticalStrut(20)); // Spazio prima del pulsante
            JButton backButton = button("Back", () -> this.getController().userClickedBack());
            mainPanel.add(backButton);
    
            // Creazione di un JScrollPane per rendere il pannello scorrevole
            JScrollPane scrollPane = new JScrollPane(mainPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
            // Aggiunta dello JScrollPane al contenitore principale
            cp.add(scrollPane);
        });
    }

    public void showUserError(String message) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            cp.add(Box.createVerticalGlue());
            cp.add(new JLabel(message, SwingConstants.CENTER));
            cp.add(Box.createVerticalStrut(10));

            JButton backButton = button("Back", () -> this.getController().userClickedBack());
            cp.add(backButton);

            cp.add(Box.createVerticalGlue());
        });
    }

    public void showAdminError(String message) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            cp.add(Box.createVerticalGlue());
            cp.add(new JLabel(message, SwingConstants.CENTER));
            cp.add(Box.createVerticalStrut(10));

            JButton backButton = button("Back", () -> this.getController().adminClickedBack());
            cp.add(backButton);

            cp.add(Box.createVerticalGlue());
        });
    }

    public void showVotePage(String username) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            cp.add(Box.createVerticalGlue());
            cp.add(new JLabel("Vota Inserzione", SwingConstants.CENTER));
            cp.add(Box.createVerticalStrut(10));

            JComboBox<Integer> insertionIdComboBox = createInsertionIdComboBox();
            cp.add(insertionIdComboBox);

            JButton upvoteButton = button("Upvote", () -> {
                Integer selectedId = (Integer) insertionIdComboBox.getSelectedItem();
                if (selectedId != null) {
                    getController().voteInsertion(selectedId, username, true);
                    this.getController().loadInitialPage();
                } else {
                    JOptionPane.showMessageDialog(cp, "Seleziona un'inserzione.");
                }
            });
            cp.add(upvoteButton);

            JButton downvoteButton = button("Downvote", () -> {
                Integer selectedId = (Integer) insertionIdComboBox.getSelectedItem();
                if (selectedId != null) {
                    getController().voteInsertion(selectedId, username, false);
                    this.getController().loadInitialPage();
                } else {
                    JOptionPane.showMessageDialog(cp, "Seleziona un'inserzione.");
                }
            });
            cp.add(downvoteButton);

            JButton backButton = button("Back", () -> this.getController().loadInitialPage());
            cp.add(backButton);

            cp.add(Box.createVerticalGlue());
        });
    }

    public void showCommentPage(String username) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            cp.add(Box.createVerticalGlue());
            cp.add(new JLabel("Commenta Inserzione", SwingConstants.CENTER));
            cp.add(Box.createVerticalStrut(10));

            JComboBox<Integer> insertionIdComboBox = createInsertionIdComboBox();
            cp.add(insertionIdComboBox);

            JTextArea commentArea = new JTextArea(5, 20);
            cp.add(new JScrollPane(commentArea));

            JButton commentButton = button("Commenta", () -> {
                Integer selectedId = (Integer) insertionIdComboBox.getSelectedItem();
                String comment = commentArea.getText();
                if (selectedId != null && !comment.isEmpty()) {
                    getController().commentInsertion(selectedId, username, comment);
                    this.getController().loadInitialPage();
                } else {
                    JOptionPane.showMessageDialog(cp, "Seleziona un'inserzione e inserisci un commento.");
                }
            });
            cp.add(commentButton);

            JButton backButton = button("Back", () -> this.getController().loadInitialPage());
            cp.add(backButton);

            cp.add(Box.createVerticalGlue());
        });
    }

    public void showReportInsertionPage(String username) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            cp.add(Box.createVerticalGlue());
            cp.add(new JLabel("Segnala Inserzione", SwingConstants.CENTER));
            cp.add(Box.createVerticalStrut(10));

            JComboBox<Integer> insertionIdComboBox = createInsertionIdComboBox();
            cp.add(insertionIdComboBox);

            JTextArea reasonArea = new JTextArea(5, 20);
            cp.add(new JScrollPane(reasonArea));

            JButton reportButton = button("Segnala", () -> {
                Integer selectedId = (Integer) insertionIdComboBox.getSelectedItem();
                String reason = reasonArea.getText();
                if (selectedId != null && !reason.isEmpty()) {
                    getController().reportInsertion(selectedId, username, reason);
                    this.getController().loadInitialPage();
                } else {
                    JOptionPane.showMessageDialog(cp, "Seleziona un'inserzione e inserisci un motivo.");
                }
            });
            cp.add(reportButton);

            JButton backButton = button("Back", () -> this.getController().loadInitialPage());
            cp.add(backButton);

            cp.add(Box.createVerticalGlue());
        });
    }

    public void showReportCommentPage(String username) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            cp.add(Box.createVerticalGlue());
            cp.add(new JLabel("Segnala Commento", SwingConstants.CENTER));
            cp.add(Box.createVerticalStrut(10));

            JComboBox<Integer> commentIdComboBox = new JComboBox<>();
            List<Integer> commentIds = this.getController().getAllCommentIds();

            for (Integer id : commentIds) {
                commentIdComboBox.addItem(id);
            }
            cp.add(commentIdComboBox);

            JTextArea reasonArea = new JTextArea(5, 20);
            cp.add(new JScrollPane(reasonArea));

            JButton reportButton = button("Segnala", () -> {
                Integer selectedId = (Integer) commentIdComboBox.getSelectedItem();
                String reason = reasonArea.getText();
                if (selectedId != null && !reason.isEmpty()) {
                    getController().reportComment(selectedId, username, reason);
                    this.getController().loadInitialPage();
                } else {
                    JOptionPane.showMessageDialog(cp, "Seleziona un commento e inserisci un motivo.");
                }
            });
            cp.add(reportButton);

            JButton backButton = button("Back", () -> this.getController().loadInitialPage());
            cp.add(backButton);

            cp.add(Box.createVerticalGlue());
        });
    }

    public void showDownloadPage(String username) {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            cp.add(Box.createVerticalGlue());
            cp.add(new JLabel("Scarica Inserzione", SwingConstants.CENTER));
            cp.add(Box.createVerticalStrut(10));

            JComboBox<Integer> insertionIdComboBox = createInsertionIdComboBox();
            cp.add(insertionIdComboBox);

            JButton downloadButton = button("Scarica", () -> {
                Integer selectedId = (Integer) insertionIdComboBox.getSelectedItem();
                if (selectedId != null) {
                    getController().downloadInsertion(selectedId, username);
                    this.getController().loadInitialPage();
                } else {
                    JOptionPane.showMessageDialog(cp, "Seleziona un'inserzione.");
                }
            });
            cp.add(downloadButton);

            JButton backButton = button("Back", () -> this.getController().loadInitialPage());
            cp.add(backButton);

            cp.add(Box.createVerticalGlue());
        });
    }

    public void showCategoryPage() {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            cp.add(Box.createVerticalGlue());
            cp.add(new JLabel("Vedi Categoria", SwingConstants.CENTER));
            cp.add(Box.createVerticalStrut(10));

            JComboBox<Integer> insertionIdComboBox = createInsertionIdComboBox();
            cp.add(insertionIdComboBox);

            JButton viewCategoryButton = button("Vedi Categoria", () -> {
                Integer selectedId = (Integer) insertionIdComboBox.getSelectedItem();
                if (selectedId != null) {
                    this.getController().handleViewCategory(selectedId);
                } else {
                    JOptionPane.showMessageDialog(cp, "Seleziona un'inserzione.");
                }
            });
            cp.add(viewCategoryButton);

            JButton backButton = button("Back", () -> this.getController().loadInitialPage());
            cp.add(backButton);

            cp.add(Box.createVerticalGlue());
        });
    }

    public void showSubcategoryPage() {
        freshPane(cp -> {
            cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

            cp.add(Box.createVerticalGlue());
            cp.add(new JLabel("Vedi Sottocategoria", SwingConstants.CENTER));
            cp.add(Box.createVerticalStrut(10));

            JComboBox<Integer> insertionIdComboBox = createInsertionIdComboBox();
            cp.add(insertionIdComboBox);

            JButton viewSubcategoryButton = button("Vedi Sottocategoria", () -> {
                Integer selectedId = (Integer) insertionIdComboBox.getSelectedItem();
                if (selectedId != null) {
                    this.getController().handleViewSubcategory(selectedId);
                } else {
                    JOptionPane.showMessageDialog(cp, "Seleziona un'inserzione.");
                }
            });
            cp.add(viewSubcategoryButton);

            JButton backButton = button("Back", () -> this.getController().loadInitialPage());
            cp.add(backButton);

            cp.add(Box.createVerticalGlue());
        });
    }

    private JButton button(String label, Runnable action) {
        var button = new JButton(label);
        button.addActionListener(event -> {
            button.setEnabled(false);
            SwingUtilities.invokeLater(() -> {
                action.run();
                button.setEnabled(true);
            });
        });
        return button;
    }

    private JLabel clickableLabel(String labelText, Runnable action) {
        var label = new JLabel(labelText);
        label.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        SwingUtilities.invokeLater(() -> {
                            action.run();
                        });
                    }
                });
        return label;
    }

    private void freshPane(Consumer<Container> consumer) {
        var cp = this.mainFrame.getContentPane();
        cp.removeAll();
        consumer.accept(cp);
        cp.validate();
        cp.repaint();
        this.mainFrame.pack();
    }

    private void addLabeledTextField(JPanel panel, String labelText, JTextField textField) {
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel(labelText));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height));
        panel.add(textField);
    }

    private Integer[] createIntegerArray(int start, int end) {
        Integer[] array = new Integer[end - start + 1];
        for (int i = start; i <= end; i++) {
            array[i - start] = i;
        }
        return array;
    }

    private void addLabeledComboBox(JPanel panel, String label, JComboBox<Integer> comboBox) {
        panel.add(new JLabel(label));
        panel.add(comboBox);
    }

    private JComboBox<Integer> createInsertionIdComboBox() {
        List<Integer> insertionIds = this.getController().getAllCreationIds();
        JComboBox<Integer> comboBox = new JComboBox<>(insertionIds.toArray(new Integer[0]));
        return comboBox;
    }

}
