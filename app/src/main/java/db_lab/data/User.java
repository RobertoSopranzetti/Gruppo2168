package db_lab.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class User {

    public final String username;
    public final String password;
    public final String email;
    public final String firstName;
    public final String lastName;
    public final int totalUpvotes;
    public final int totalDownvotes;
    public final boolean reported;

    public User(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalUpvotes = 0;
        this.totalDownvotes = 0;
        this.reported = false;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (other == null) {
            return false;
        } else if (other instanceof User) {
            var p = (User) other;
            return (p.username.equals(this.username) && p.password.equals(this.password) && p.email.equals(this.email)
                    && p.firstName.equals(this.firstName) && p.lastName.equals(this.lastName)
                    && p.totalUpvotes == this.totalUpvotes && p.totalDownvotes == this.totalDownvotes
                    && p.reported == this.reported);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.username, this.password, this.email, this.firstName, this.lastName, this.totalUpvotes,
                this.totalDownvotes, this.reported);
    }

    @Override
    public String toString() {
        return Printer.stringify(
                "User: ",
                List.of(Printer.field("username", this.username), Printer.field("password", this.password),
                        Printer.field("email", this.email), Printer.field("firstName", this.firstName),
                        Printer.field("lastName", this.lastName), Printer.field("totalUpvotes", this.totalUpvotes),
                        Printer.field("totalDownvotes", this.totalDownvotes),
                        Printer.field("reported", this.reported)));
    }

    public final class DAO {

        public static void signUpUser(Connection connection, String username, String password, String nome,
                String cognome, String email) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.USER_SIGN_UP)) {
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, nome);
                statement.setString(4, cognome);
                statement.setString(5, email);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void createCollection(Connection connection, String nome, boolean privata, String username) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.CREATE_COLLECTION)) {
                statement.setString(1, nome);
                statement.setBoolean(2, privata);
                statement.setString(3, username);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void downloadCreation(Connection connection, int idInserzione, String username) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.DOWNLOAD_CREATION)) {
                statement.setInt(1, idInserzione);
                statement.setString(2, username);
                statement.setInt(3, idInserzione);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void voteCreation(Connection connection, int idInserzione, String username, boolean tipo) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.VOTE_CREATION)) {
                statement.setInt(1, idInserzione);
                statement.setString(2, username);
                statement.setBoolean(3, tipo);
                statement.setBoolean(4, tipo);
                statement.setBoolean(5, tipo);
                statement.setInt(6, idInserzione);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void commentCreation(Connection connection, int idInserzione, String username, String testo) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.COMMENT_CREATION)) {
                statement.setString(1, testo);
                statement.setInt(2, idInserzione);
                statement.setString(3, username);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void reportInsertion(Connection connection, int idInserzione, String username, String motivo) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.REPORT_INSERTION)) {
                statement.setInt(1, idInserzione);
                statement.setString(2, username);
                statement.setString(3, motivo);
                statement.setInt(4, idInserzione);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void reportComment(Connection connection, int idCommento, String username, String motivo) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.REPORT_COMMENT)) {
                statement.setInt(1, idCommento);
                statement.setString(2, username);
                statement.setString(3, motivo);
                statement.setInt(4, idCommento);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static List<String[]> topCreators(Connection connection) {
            List<String[]> result = new ArrayList<>();
            String query = Queries.TOP_CREATORS;
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String username = resultSet.getString("Username");
                        int votiTotali = resultSet.getInt("VotiTotali");
                        result.add(new String[] { username, String.valueOf(votiTotali) });
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }

        public static List<Creation> topCreations(Connection connection) {
            List<Creation> result = new ArrayList<>();
            String query = Queries.TOP_CREATIONS;
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int idCreazione = resultSet.getInt("IDcreazione");
                        String nome = resultSet.getString("Nome");
                        String descrizione = resultSet.getString("Descrizione");
                        int forza = resultSet.getInt("Forza");
                        int destrezza = resultSet.getInt("Destrezza");
                        int costituzione = resultSet.getInt("Costituzione");
                        int intelligenza = resultSet.getInt("Intelligenza");
                        int saggezza = resultSet.getInt("Saggezza");
                        int carisma = resultSet.getInt("Carisma");
                        String classe = resultSet.getString("Classe");
                        String razza = resultSet.getString("Razza");
                        int livello = resultSet.getInt("Livello");
                        String taglia = resultSet.getString("Taglia");
                        int difficolta = resultSet.getInt("Difficolta");
                        String tipo = resultSet.getString("Tipo");

                        result.add(new Creation(
                                idCreazione,
                                nome,
                                descrizione,
                                forza,
                                destrezza,
                                costituzione,
                                intelligenza,
                                saggezza,
                                carisma,
                                classe != null ? classe : "",
                                razza != null ? razza : "",
                                livello,
                                taglia != null ? taglia : "",
                                difficolta,
                                tipo != null ? tipo : ""));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }

        public static void addSubcategory(Connection connection, String nome, String descrizione, String username) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.ADD_SUBCATEGORY)) {
                statement.setString(1, nome);
                statement.setString(2, descrizione);
                statement.setString(3, username);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void addToSubcategory(Connection connection, int idInserzione, int idSottocategoria) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.ADD_TO_SUBCATEGORY)) {
                statement.setInt(1, idInserzione);
                statement.setInt(2, idSottocategoria);
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
            try (PreparedStatement statement = connection.prepareStatement(Queries.USER_MODERATION)) {
                statement.setString(1, adminName);
                statement.setString(2, username);
                statement.setString(3, tipo);
                statement.setString(4, username);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}