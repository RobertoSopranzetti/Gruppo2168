package db_lab.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public final class Admin {

    public final String username;
    public final String password;
    public final String email;
    public final String firstName;
    public final String lastName;

    public Admin(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Admin)) {
            return false;
        }
        Admin other = (Admin) obj;
        return this.username.equals(other.username) &&
                this.password.equals(other.password) &&
                this.email.equals(other.email) &&
                this.firstName.equals(other.firstName) &&
                this.lastName.equals(other.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.username, this.password, this.email, this.firstName, this.lastName);
    }

    @Override
    public String toString() {
        return Printer.stringify("Admin", List.of(
                Printer.field("username", this.username),
                Printer.field("password", this.password),
                Printer.field("email", this.email),
                Printer.field("firstName", this.firstName),
                Printer.field("lastName", this.lastName)));
    }

    public final class DAO {

        public static void signUpAdmin(Connection connection, String username, String password, String nome,
                String cognome, String email) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.ADMIN_SIGN_UP)) {
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
    }
}