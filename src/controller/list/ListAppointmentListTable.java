package controller.list;

import controller.BaseController;
import controller.other.RunEvent;
import controller.other.FilterFrame;
import controller.other.MainFrame;
import controller.detail.DetailAppointmentFrameBuilder;
import javafx.scene.control.Alert;
import model.db.Appointment;
import model.db.Contact;
import model.db.Customer;
import model.base.BaseModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ListAppointmentListTable extends ListTable<Appointment> implements Initializable {
    private final FilterFrame filterController = new FilterFrame();
    private final HashMap<Long, Contact> contactMap = new HashMap<>();
    private FilterFrame.FilterItem currentFilter = null;

    public ListAppointmentListTable(ObservableList<Customer> customers, RunEvent runEvent) {
        super(new DetailAppointmentFrameBuilder(Appointment.class), runEvent);
        ((DetailAppointmentFrameBuilder) detailFrameBuilder).setContacts(Collections.unmodifiableMap(contactMap));
        ((DetailAppointmentFrameBuilder) detailFrameBuilder).setCustomerList(Collections.unmodifiableList(customers));
        runEvent.addListener(MainFrame.Event.CustomerDeleted, this::loadTable);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        btnFilter.setDisable(false);
        btnFilter.setVisible(true);
    }

    /**
     * add column
     */
    @Override
    protected void addColumns() {
        TableColumn<Appointment, String> colContact = new TableColumn<>(BaseController.resourceBundle.getString("appointment.contact"));
        colContact.setCellValueFactory(param -> new SimpleStringProperty(Optional.ofNullable(
                contactMap.get(param.getValue().getContactID())).map(Contact::getName).orElse("")));

        TableColumn<Appointment, String> colStartDate = new TableColumn<>(BaseController.resourceBundle.getString("appointment.start"));
        colStartDate.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStartDateLocaleString()));

        TableColumn<Appointment, String> colEndDate = new TableColumn<>(BaseController.resourceBundle.getString("appointment.end"));
        colEndDate.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getEndDateLocaleString()));

        TableColumn<Appointment, String> colCustomerID = new TableColumn<>(BaseController.resourceBundle.getString("appointment.customerId"));
        colCustomerID.setCellValueFactory(param -> new SimpleStringProperty(nonZero(param.getValue().getCustomerID())));

        tblView.getColumns().addAll(getTableColumn(Appointment.class, "title"),
                getTableColumn(Appointment.class, "description"),
                getTableColumn(Appointment.class, "location"),
                colContact,
                getTableColumn(Appointment.class, "type"),
                colStartDate,
                colEndDate,
                colCustomerID);
    }

    /**
     * @see ListTable#loadData()
     */
    @Override
    protected final void loadData() {
        loadTable();
        executeQuery("SELECT * FROM contacts", this::buildContactMap);
    }

    /**
     * load data
     */
    private void loadTable() {
        List<Object> arguments = null;
        String query = "SELECT Appointment_ID, Title, Description, `Location`, `Type`, `Start`, " +
                    "`End`, Customer_ID, User_ID, Contact_ID " +
                    "FROM appointments";

        tblView.getItems().clear();
        if (currentFilter != null) {
            query += String.format(" WHERE YEAR(`Start`) = ? AND %s(`Start`) = ?", currentFilter.field);
            arguments = toArray(currentFilter.year, currentFilter.fieldValue);
        }

        executeQuery(query, arguments, this::parseAppointments);
    }

    /**
     * parse appointment
     *
     * @param e a sql exception from the query
     * @param rs the result set containing the appointment rows
     */
    private void parseAppointments(SQLException e, ResultSet rs) {
        if (e != null)
            return;

        ObservableList<Appointment> appointments = tblView.getItems();
        appointments.clear();

        try {
            while (rs.next())
                appointments.add(new Appointment(rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getTimestamp(6).toLocalDateTime(),
                        rs.getTimestamp(7).toLocalDateTime(),
                        rs.getLong(8),
                        rs.getLong(9),
                        rs.getLong(10)));
        } catch (SQLException exception) {
            printSQLException(exception);
        }
    }

    /**
     * build contact list
     *
     * @param e a sql exception from the query
     * @param rs the result set containing the contact rows
     */
    private void buildContactMap(SQLException e, ResultSet rs) {
        if (e != null)
            return;

        try {
            while (rs.next()) {
                final Contact contact = new Contact(rs.getLong(1), rs.getString(2), rs.getString((3)));
                contactMap.put(contact.getID(), contact);
            }
        } catch (SQLException exception) {
            printSQLException(exception);
        }
    }

    /**
     * @see ListTable#getInsertQuery()
     */
    @Override
    protected String getInsertQuery() {
        return "INSERT INTO appointments (Title, Description, `Location`, `Type`, `Start`, `End`, Customer_ID, User_ID, Contact_ID, Created_By, Last_Updated_By) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    /**
     * @see ListTable#getNewRow()
     */
    @Override
    protected Appointment getNewRow() {
        return new Appointment(0, null, null, null, null, null, null, 0, 0, 0);
    }

    /**
     * @see ListTable#getUpdateQuery()
     */
    @Override
    protected String getUpdateQuery() {
        return "UPDATE appointments " +
                "SET Title = ?, Description = ?, `Location` = ?, `Type` = ?, `Start` = ?, `End` = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ?, Last_Updated_By = ?, Last_Update = NOW() " +
                "WHERE Appointment_ID = ?";
    }

    /**
     * @see ListTable#deleteCustomer(BaseModel)
     */
    @Override
    protected boolean deleteCustomer(Appointment row) {
        return true;
    }

    /**
     * @see ListTable#getDeleteQuery()
     */
    @Override
    protected String getDeleteQuery() {
        return "DELETE FROM appointments WHERE Appointment_ID = ?";
    }

    @Override
    protected void runEvent() {
    }

    /**
     * @see ListTable#getDeletedStatus(BaseModel)
     */
    @Override
    protected String getDeletedStatus(Appointment row) {
        return BaseController.resourceBundle.getString("row.deleted.message").replace("%{row}", String.format("%s (%s: %d, %s: %s)",
                BaseController.resourceBundle.getString("appointment.appointment"),
                BaseController.resourceBundle.getString("row.id"),
                row.getID(),
                BaseController.resourceBundle.getString("appointment.type"),
                row.getType()));

    }

    /**
     * if the passed in value is 0, it return an empty string for display in the table, otherwise it stringifies the
     * long
     *
     * @param val the long to stringify
     * @return the string value for the table
     */
    protected String nonZero(long val) {
        return val == 0 ? "" : Long.toString(val);
    }

    /**
     * add filter
     * @see ListTable#addFilter()
     */
    @Override
    protected void addFilter() {
        filterController.openFilterWindow((fields) -> {
            currentFilter = fields;
            loadData();
        });
    }

    /**
     * check updatable
     * @see ListTable#canUpdate(BaseModel)
     */
    @Override
    protected boolean canUpdate(Appointment row) {
        String query = "SELECT COUNT(*) FROM appointments " +
                "WHERE (UNIX_TIMESTAMP(`START`) BETWEEN UNIX_TIMESTAMP(?) AND UNIX_TIMESTAMP(?)" +
                "OR UNIX_TIMESTAMP(`END`) BETWEEN UNIX_TIMESTAMP(?) AND UNIX_TIMESTAMP(?)) " +
                "AND Customer_ID = ?";
        List<Object> arguments = toArray(row.getStartDateUTCString(),
                row.getEndDateUTCString(),
                row.getStartDateUTCString(),
                row.getEndDateUTCString(),
                row.getCustomerID());

        if (row.getID() != 0L) {
            query += " AND Appointment_Id != ?";
            arguments.add(row.getID());
        }

        boolean noOverlaps = executeQuery(query, arguments, (ex, rs) -> {
            if (ex != null)
                return false;

            try {
                rs.next();
                return rs.getInt("COUNT(*)") == 0;
            } catch (SQLException exception) {
                printSQLException(exception);
                return false;
            }
        });

        if (!noOverlaps)
            showMessageBox(resourceBundle.getString("error.defaultTitle"), BaseController.resourceBundle.getString("error.overlapping"), Alert.AlertType.ERROR);

        return noOverlaps;
    }
}
