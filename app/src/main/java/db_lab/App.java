package db_lab;

import java.sql.SQLException;

import db_lab.data.DAOUtils;
import db_lab.model.Model;

public final class App {

    public static void main(String[] args) throws SQLException {

        var connection = DAOUtils.localMySQLConnection("beasttavern", "root", "xroby985");
        var model = Model.fromConnection(connection);
        var view = new View(() -> {
            // We want to make sure we close the connection when we're done
            // with our application.
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        var controller = new Controller(model, view);
        view.setController(controller);
        controller.loadInitialPage();
    }
}