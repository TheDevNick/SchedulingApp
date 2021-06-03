package controller;

import javafx.scene.control.Alert;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URI;
import java.sql.*;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * an abstract base controller class with helper methods that can be used across controllers
 */
abstract public class BaseController {
    protected static ResourceBundle resourceBundle;
    protected static long userID = 0L;
    private static Locale currentLocale = currentLocale();
    protected ViewController viewController;
    protected static Connection connection;

    {
        if (connection == null) {
            try {
                createDBConnection();
            } catch (SQLException ex) {
                printSQLException(ex);
                connection = null;
            }
        }
    }

    /**
     * set the locale
     * @return get locale settled
     */
    private static Locale currentLocale() {
        Locale locale = Locale.getDefault();
        switch (locale.getLanguage()) {
            case "en":
            case "fr":
                break;
            default:
                locale = new Locale("en", "US");
        }
        return locale;
    }

    /**
     * @return get current locale
     */
    public static Locale getLocale() {
        return currentLocale;
    }

    /**
     * @return get resource bundle
     */
    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * set locale and bundle
     */
    public static void setLocaleAndBundle() {
        currentLocale = currentLocale();
        resourceBundle = ResourceBundle.getBundle("App", getLocale());
    }

    /**
     * parse database.xml file to get connection string
     *
     * @return the database connection string as parsed from the database.xml file
     * @throws Exception any IO or parsing exception
     */
    private String getConnectionString() throws Exception {
        final Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(new File("database.xml"));
        doc.getDocumentElement().normalize();

        return new URI(
                "jdbc:mysql",
                null,
                doc.getElementsByTagName("server").item(0).getTextContent(),
                Integer.parseInt(doc.getElementsByTagName("port").item(0).getTextContent()),
                "/" + doc.getElementsByTagName("name").item(0).getTextContent(),
                String.format("user=%s&password=%s",
                        doc.getElementsByTagName("user").item(0).getTextContent(),
                        doc.getElementsByTagName("password").item(0).getTextContent()),
                null).toString();
    }

    /**
     * create connection to db
     * @return the connection object
     * @throws SQLException any exception that occurs when trying to connect to the DB
     */
    private Connection createDBConnection() throws SQLException {
        if (connection != null && !connection.isClosed())
            return connection;

        connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(getConnectionString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return connection;
    }

    /**
     * execute query
     * @param <T>     value the handler returns
     * @param query   query string
     * @param function handler function
     * @return result from the handler
     * @see BaseController#executeQuery(String, List, BiFunction)
     */
    protected <T> T executeQuery(String query, BiFunction<SQLException, ResultSet, T> function) {
        return executeQuery(query, null, (ex, rs) -> (T) function.apply(ex, rs));
    }

    /**
     * execute query
     * @param query   query string
     * @param arguments an array of arguments
     * @param function handler function
     * @see BaseController#executeQuery(String, List, BiFunction)
     */
    protected void executeQuery(String query, List<Object> arguments, BiConsumer<SQLException, ResultSet> function) {
        executeQuery(query, arguments, (ex, rs) -> {
            function.accept(ex, rs);
            return null;
        });
    }

    /**
     * execute query
     * @param query   query string
     * @param function handler function
     * @see BaseController#executeQuery(String, List, BiFunction)
     */
    protected void executeQuery(String query, BiConsumer<SQLException, ResultSet> function) {
        executeQuery(query, null, (ex, rs) -> {
            function.accept(ex, rs);
            return null;
        });
    }

    /**
     * execute query
     * @param <T>     value the handler returns
     * @param query   query string
     * @param arguments an array of arguments
     * @param function handler function
     * @return result from the handler
     */
    protected <T> T executeQuery(String query, List<Object> arguments, BiFunction<SQLException, ResultSet, T> function) {
        try {
            PreparedStatement stmt = createDBConnection().prepareStatement(query);
            setArguments(stmt, arguments);
            ResultSet rs = stmt.executeQuery();
            return function.apply(null, rs);
        } catch (SQLException ex) {
            printSQLException(ex);
            return function.apply(ex, null);
        }
    }

    /**
     * execute insert query
     * @param query   query string
     * @param arguments an array of arguments
     * @param function consumer function
     */
    protected void executeInsert(String query, List<Object> arguments, BiConsumer<SQLException, Long> function) {
        try {
            Connection connection = createDBConnection();
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            setArguments(stmt, arguments);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next())
                function.accept(null, rs.getLong(1));
            else
                throw new SQLException("Creating user failed, no ID obtained.");
        } catch (SQLException ex) {
            printSQLException(ex);
            function.accept(ex, null);
        }
    }

    /**
     * execute update query
     * @param query   query string
     * @param arguments an array of arguments
     * @param function consumer function
     * @see BaseController#executeUpdate(String, List, BiFunction)
     */
    protected void executeUpdate(String query, List<Object> arguments, BiConsumer<SQLException, Integer> function) {
        executeUpdate(query, arguments, ((BiFunction<SQLException, Integer, Void>) (ex, updates) -> {
            function.accept(ex, updates);
            return null;
        }));
    }

    /**
     * execute update query
     * @param query   query string
     * @param arguments an array of arguments
     * @param function handler function
     * @param <T>       the type that is returned from the callback
     * @return whatever value the caller returns from the callback
     */
    protected <T> T executeUpdate(String query, List<Object> arguments, BiFunction<SQLException, Integer, T> function) {
        try {
            Connection connection = createDBConnection();
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            setArguments(stmt, arguments);
            return function.apply(null, stmt.executeUpdate());
        } catch (SQLException ex) {
            printSQLException(ex);
            return function.apply(ex, null);
        }
    }

    /**
     * iterates over a list of objects to use as arguments in a prepared statement
     * @param statement the prepared statement that will be executed
     * @param arguments the arguments to use with the prepared statement
     * @throws SQLException any exception that occurs when setting the arguments
     */
    private void setArguments(PreparedStatement statement, List<Object> arguments) throws SQLException {
        if (arguments != null)
            for (int i = 0; i < arguments.size(); i++)
                statement.setObject(i + 1, arguments.get(i));
    }

    /**
     * reusable method to print any sql exceptions during development
     * @param ex the exception to print
     */
    protected void printSQLException(SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("ErrorCode: " + ex.getErrorCode());
    }

    /**
     * a setter method for the view controller used by main.Main and View
     * @param viewController the controller instance
     */
    protected void setViewController(ViewController viewController) {
        this.viewController = viewController;
    }

    /**
     * Show error message box
     * @param title   title of message
     * @param message content of message
     * @param type    type of messagebox
     */
    protected void showMessageBox(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
