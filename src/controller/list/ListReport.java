package controller.list;

import controller.BaseController;
import model.db.Appointment;
import model.db.Contact;
import model.db.Customer;
import model.db.FirstLevelDivision;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controls the contents of the reports tab. Queries and formats the data for consumption
 */
public class ListReport extends BaseController {
    @FXML
    private Button btnTypeAndMonth;
    @FXML
    private Button btnSchedule;
    @FXML
    private Button btnDivision;
    @FXML
    private TextArea textArea;

    /**
     * run report methods
     *
     * @param event JavaFX action event
     */
    @FXML
    private void runReport(ActionEvent event) {
        textArea.clear();
        String button = ((Button) event.getSource()).getId();
        String report;
        switch (button) {
            case "btnTypeAndMonth":
                report = reportMonthAndMode();
                break;
            case "btnSchedule":
                report = reportContactAndAppointment();
                break;
            case "btnDivision":
                report = reportCustomerAndDivision();
                break;
            default:
                System.out.println("unreachable unhandled report button");
                report = "";
        }
        textArea.setText(report);
    }

    /**
     * runs the first report to get the total number of appointments by month and by type
     *
     * @return the string to display
     */
    private String reportMonthAndMode() {
        return resourceBundle.getString("report.byMonth")
                + ":\n"
                + executeQuery("SELECT MONTH(`Start`) as `Month`, COUNT(*) as `Count` " +
                "FROM appointments GROUP BY MONTH(`Start`) " +
                "ORDER BY MONTH(`Start`)", this::monthCount)
                + "\n"
                + resourceBundle.getString("report.byType")
                + ":\n"
                + executeQuery("SELECT `Type`, COUNT(*) as `Count` " +
                "FROM appointments GROUP BY `Type` " +
                "ORDER BY `Type`", this::typeCount);
    }

    /**
     * parses the number of appointments by month into a human-readable format
     *
     * @param ex a sql exception from the query
     * @param rs the result set containing report values
     * @return the string to display
     */
    private String monthCount(SQLException ex, ResultSet rs) {
        if (ex != null)
            return "";

        StringBuilder output = new StringBuilder();

        try {
            while (rs.next()) {
                String month;
                month = resourceBundle.getString(String.format("month.%d", rs.getInt(1)));
                output.append(String.format("\t%s:\t%d\n", month, rs.getInt(2)));
            }
        } catch (SQLException exception) {
            printSQLException(exception);
        }
        return output.toString();
    }

    /**
     * parses the number of appointments by type into a human-readable format
     *
     * @param ex a sql exception from the query
     * @param rs the result set containing report values
     * @return the string to display
     */
    private String typeCount(SQLException ex, ResultSet rs) {
        if (ex != null)
            return "";

        StringBuilder output = new StringBuilder();

        try {
            while (rs.next())
                output.append(String.format("\t%s:\t%d\n", rs.getString(1), rs.getInt(2)));
        } catch (SQLException exception) {
            printSQLException(exception);
        }
        return output.toString();
    }

    /**
     * runs the second report to get a schedule of appointments per contact
     *
     * @return the string to display
     */
    private String reportContactAndAppointment() {
        return executeQuery("SELECT Appointment_ID, Title, Description, `Location`, `Type`, `Start`, `End`, " +
                "Customer_ID, User_ID, c.Contact_ID, c.Contact_Name, c.Email " +
                "FROM appointments a " +
                "JOIN contacts c ON c.Contact_ID = a.Contact_ID " +
                "ORDER BY Contact_ID, `Start`", this::contactAndAppointment);
    }

    /**
     * parse contact and appointments
     *
     * @param ex a sql exception from the query
     * @param rs the result set containing report values
     * @return the string to display
     */
    private String contactAndAppointment(SQLException ex, ResultSet rs) {
        if (ex != null)
            return "";
        final StringBuilder output = new StringBuilder();

        try {
            while (rs.next()) {
                long customerId = 0L;
                Appointment appointment = new Appointment(rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getTimestamp(6).toLocalDateTime(),
                        rs.getTimestamp(7).toLocalDateTime(),
                        rs.getLong(8),
                        rs.getLong(9),
                        rs.getLong(10));
                if (customerId != appointment.getCustomerID()) {
                    output.append("\n");
                    output.append(new Contact(rs.getLong(10), rs.getString(11), rs.getString(12)).viewString());
                }
                output.append(appointment.viewString());
            }
        } catch (SQLException exception) {
            printSQLException(exception);
        }

        return output.toString();
    }

    /**
     * runs the third report to get a rundown of the customers per division
     *
     * @return the string to display
     */
    private String reportCustomerAndDivision() {
        return executeQuery("SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, d.Division_ID, d.Country_ID, d.Division " +
                "FROM customers c " +
                "JOIN first_level_divisions d ON d.Division_ID = c.Division_ID " +
                "ORDER BY d.Division, c.Customer_ID", this::customerAndDivision);
    }

    /**
     * parses the divisions and customers into a human-readable string for display
     *
     * @param ex a sql exception from the query
     * @param rs the result set containing report values
     * @return the string to display
     */
    private String customerAndDivision(SQLException ex, ResultSet rs) {
        if (ex != null)
            return "";

        final StringBuilder output = new StringBuilder();

        try {
            long divisionId = 0L;
            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getLong(6)
                );

                if (divisionId != customer.getDivisionID()) {
                    divisionId = customer.getDivisionID();
                    output.append("\n");
                    output.append(new FirstLevelDivision(rs.getLong(6), rs.getString(8), rs.getLong(7)).viewString());
                }

                output.append(customer.viewString());
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return output.toString();
    }
}
