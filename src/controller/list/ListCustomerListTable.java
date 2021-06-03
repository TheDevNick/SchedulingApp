package controller.list;

import controller.BaseController;
import controller.other.RunEvent;
import controller.other.MainFrame;
import controller.detail.DetailCustomerFrameBuilder;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import model.base.BaseModel;
import model.db.Country;
import model.db.Customer;
import model.db.FirstLevelDivision;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.HashMap;

public class ListCustomerListTable extends ListTable<Customer> {
    private final HashMap<Long, FirstLevelDivision> divisionMap = new HashMap<>();
    private final HashMap<Long, Country> countryMap = new HashMap<>();

    public ListCustomerListTable(RunEvent runEvent) {
        super(new DetailCustomerFrameBuilder(Customer.class), runEvent);
        ((DetailCustomerFrameBuilder) detailFrameBuilder).setDivisions(Collections.unmodifiableMap(divisionMap));
        ((DetailCustomerFrameBuilder) detailFrameBuilder).setCountries(Collections.unmodifiableMap(countryMap));
    }

    /**
     * add column
     *
     * @see ListTable#addColumns()
     */
    @Override
    protected final void addColumns() {
        TableColumn<Customer, String> colName = getTableColumn(Customer.class, "name");
        TableColumn<Customer, String> callAddress = getTableColumn(Customer.class, "address");
        TableColumn<Customer, String> callCode = getTableColumn(Customer.class, "postalCode");
        TableColumn<Customer, String> colPhone = getTableColumn(Customer.class, "phone");
        TableColumn<Customer, String> colDivision = new TableColumn<>(BaseController.resourceBundle.getString("customer.firstLevelDivision"));

        colDivision.setCellValueFactory(param -> new SimpleStringProperty(divisionMap.get(param.getValue().getDivisionID()).getDivision()));
        TableColumn<Customer, String> colCountry = new TableColumn<>(BaseController.resourceBundle.getString("customer.country"));

        colCountry.setCellValueFactory(param -> new SimpleStringProperty(countryMap.get(divisionMap.get(param.getValue().getDivisionID()).getCountryID()).getCountry()));
        tblView.getColumns().addAll(colName, callAddress, callCode, colPhone, colDivision, colCountry);
    }

    /**
     * load data
     *
     * @see ListTable#loadData()
     */
    @Override
    protected final void loadData() {
        executeQuery("SELECT Division_ID, Division, Country_ID FROM first_level_divisions", (ex, rs) -> {
            if (ex == null)
                divisionMap(rs);
        });

        executeQuery("SELECT Country_ID, Country FROM countries", (ex, rs) -> {
            if (ex == null)
                addCountries(rs);
        });

        executeQuery("SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, d.Division_ID, d.Country_ID FROM customers c JOIN first_level_divisions d ON d.Division_ID = c.Division_ID;", (ex, rs) -> {
            if (ex == null)
                consumeResultSet(rs);
        });
        executeQuery("SELECT Appointment_ID, `Start` FROM appointments WHERE `Start` BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 15 MINUTE) AND User_ID = ?", toArray(BaseController.userID), this::appointmentNotify);
    }

    /**
     * notify appointment
     *
     * @param e a sql exception from the query
     * @param rs the result set containing the appointment rows
     */
    private void appointmentNotify(SQLException e, ResultSet rs) {
        if (e != null)
            return;

        StringBuilder appointments = new StringBuilder();
        try {
            while (rs.next())
                appointments
                        .append("\n")
                        .append(BaseController.resourceBundle.getString("row.id"))
                        .append(" ")
                        .append(rs.getInt(1))
                        .append(" ")
                        .append(BaseController.resourceBundle.getString("appointment.at"))
                        .append(" ")
                        .append(rs.getTimestamp(2).toLocalDateTime()
                                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                                        .withLocale(BaseModel.locale)));

            final String content = appointments.length() != 0
                    ? BaseController.resourceBundle.getString("appointment.upcomingAppointment") + "\n" + appointments
                    : BaseController.resourceBundle.getString("appointment.noUpcomingAppointment");

            showMessageBox(BaseController.resourceBundle.getString("appointment.alertTitle"), content, Alert.AlertType.INFORMATION);
        } catch (SQLException exception) {
            printSQLException(exception);
        }
    }

    private void divisionMap(ResultSet rs) {
        try {
            while (rs.next()) {
                final FirstLevelDivision firstLevelDivision = new FirstLevelDivision(rs.getInt(1), rs.getString(2), rs.getInt(3));
                divisionMap.put(firstLevelDivision.getID(), firstLevelDivision);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    private void addCountries(ResultSet rs) {
        try {
            while (rs.next()) {
                final Country country = new Country(rs.getInt(1), rs.getString(2));
                countryMap.put(country.getID(), country);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    /**
     * @see ListTable#getNewRow()
     */
    @Override
    protected Customer getNewRow() {
        return new Customer(0, "", "", "", "", 0);
    }

    @Override
    protected boolean canUpdate(Customer row) {
        return true;
    }

    private void consumeResultSet(ResultSet rs) {
        try {
            while (rs.next())
                tblView.getItems().add(new Customer(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getInt(6)
                ));
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    /**
     * @see ListTable#getInsertQuery()
     */
    @Override
    public String getInsertQuery() {
        return "INSERT INTO customers " +
                "(Customer_Name, Address, Postal_Code, Phone, Division_ID, Created_By, Last_Updated_By) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    /**
     * @see ListTable#getUpdateQuery()
     */
    @Override
    public String getUpdateQuery() {
        return "UPDATE customers " +
                "SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ?, Last_Updated_By = ?, Last_Update = NOW() " +
                "WHERE Customer_ID = ?";
    }

    /**
     * @see ListTable#getDeleteQuery()
     */
    @Override
    public String getDeleteQuery() {
        return "DELETE FROM customers WHERE Customer_ID = ?";
    }

    /**
     * delete customer
     * @param row the row whose dependencies need to be deleted
     * @return result
     */
    @Override
    protected boolean deleteCustomer(Customer row) {
        return executeUpdate("DELETE FROM appointments WHERE Customer_ID = ?", toArray(row.getID()), (ex, updates) -> ex == null);
    }

    /**
     * delete status
     */
    @Override
    protected String getDeletedStatus(Customer row) {
        String appointments = executeQuery("SELECT Appointment_ID, Type FROM appointments WHERE Customer_ID = ?",
                toArray(row.getID()),
                this::parseAppointments);

        String message = BaseController.resourceBundle.getString("row.deleted.message").replace("%{row}", BaseController.resourceBundle.getString("customer.customer"));

        if (appointments.length() != 0)
            message += "\n\n" + BaseController.resourceBundle.getString("appointment.deleted") + "\n" + appointments;

        return message;
    }

    /**
     * parses the appointments that got deleted in association with a customer row
     *
     * @param e a sql exception from the query
     * @param rs the result set containing the appointment rows
     * @return the string to display
     */
    private String parseAppointments(SQLException e, ResultSet rs) {
        if (e != null)
            return "";

        StringBuilder output = new StringBuilder();

        try {
            while (rs.next())
                output.append(String.format("%s: %d, %s: %s\n",
                        BaseController.resourceBundle.getString("row.id"),
                        rs.getInt(1),
                        BaseController.resourceBundle.getString("appointment.type"),
                        rs.getString(2)));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return output.toString();
    }

    /**
     * @see ListTable#runEvent()
     */
    @Override
    protected void runEvent() {
        runEvent.emit(MainFrame.Event.CustomerDeleted);
    }
}
