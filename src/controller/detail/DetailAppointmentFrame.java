package controller.detail;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.base.BaseModel;
import model.db.Appointment;
import model.db.Contact;
import model.db.Customer;
import model.db.User;

import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.function.Function;

public class DetailAppointmentFrame extends DetailFrame<Appointment> {
    final boolean is24Hour;
    private final Map<Long, Contact> contacts;
    private final TreeMap<Long, Customer> customers;
    private final TreeMap<Long, User> users;

    @FXML
    private TextField tftitle;
    @FXML
    private TextField tfdescription;
    @FXML
    private TextField tflocation;
    @FXML
    private TextField tftype;
    @FXML
    private ComboBox<User> cmbUser;
    @FXML
    private ComboBox<Contact> cmbContact;
    @FXML
    private ComboBox<Customer> cmbCustomer;
    @FXML
    private ComboBox<String> cmbStartHour;
    @FXML
    private ComboBox<String> cmbStartMinute;
    @FXML
    private ComboBox<String> cmbEndHour;
    @FXML
    private ComboBox<String> cmbEndMinute;
    @FXML
    private ChoiceBox<String> choiceStart;
    @FXML
    private ChoiceBox<String> choiceEnd;
    @FXML
    private DatePicker pickerStart;
    @FXML
    private DatePicker pickerEnd;

    public DetailAppointmentFrame(String title,
                                  Map<Long, Contact> contacts,
                                  List<Customer> customers,
                                  FrameType frameType,
                                  Appointment row,
                                  Function<Appointment, Boolean> function) {
        super(title, frameType, row, function);
        is24Hour = LocalTime.of(23, 0)
                .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
                .matches("^23.+00$");

        this.contacts = contacts;
        this.users = new TreeMap<>();

        this.customers = new TreeMap<>();
        for (Customer customer : customers)
            this.customers.put(customer.getID(), customer);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUsers();
        initializeDateFields();
        cmbContact.getItems().addAll(contacts.values());
        cmbCustomer.getItems().addAll(customers.values());
        super.initialize(url, resourceBundle);
    }

    /**
     * @see DetailFrame#applyAllToRow()
     */
    @Override
    protected void applyAllToRow() {
        LocalDateTime start = parseDateTime(pickerStart, cmbStartHour, cmbStartMinute, choiceStart);
        LocalDateTime end = parseDateTime(pickerEnd, cmbEndHour, cmbEndMinute, choiceEnd);
        row.setStart(start);
        row.setEnd(end);
        row.setCustomerID(getRowID(cmbCustomer.getValue()));
        row.setUserID(getRowID(cmbUser.getValue()));
        row.setContactID(getRowID(cmbContact.getValue()));
    }

    /**
     * @see DetailFrame#setFields()
     */
    @Override
    protected void setFields() {
        setCMBFromMap(cmbContact, contacts, row.getContactID());
        setCMBFromMap(cmbCustomer, customers, row.getCustomerID());
        setCMBFromMap(cmbUser, users, row.getUserID());
        setDateValues(row.getLocalStart(), pickerStart, cmbStartHour, cmbStartMinute, choiceStart);
        setDateValues(row.getLocalEnd(), pickerEnd, cmbEndHour, cmbEndMinute, choiceEnd);
    }

    /**
     * @see DetailFrame#getResourceURL()
     */
    @Override
    protected String getResourceURL() {
        return "/view/AppointmentFrame.fxml";
    }

    /**
     * @see DetailFrame#getWidth()
     */
    @Override
    protected double getWidth() {
        return 1024;
    }

    /**
     * @see DetailFrame#getHeight()
     */
    @Override
    protected double getHeight() {
        return 768;
    }

    private <T extends BaseModel> void setCMBFromMap(ComboBox<T> comboBox, Map<Long, T> map, long id) {
        if (id != 0)
            comboBox.getSelectionModel().select(map.get(id));

        comboBox.setDisable(isReadOnly);
    }

    /**
     * sets the date and time fields from the ZonedDateTime object
     *
     * @param date           the object that holds the date and time
     * @param datePicker     a date picker for the date
     * @param cmbHour     a ComboBox for the hour
     * @param cmbMin   a ComboBox for the minute
     * @param choice a ChoiceBox for am/pm
     */
    private void setDateValues(ZonedDateTime date,
                               DatePicker datePicker,
                               ComboBox<String> cmbHour,
                               ComboBox<String> cmbMin,
                               ChoiceBox<String> choice) {
        if (is24Hour) {
            cmbHour.setValue(String.format("%02d", date.getHour()));
        } else {
            final int modHour = date.getHour() % 12;
            cmbHour.setValue(Integer.toString(modHour == 0 ? 12 : modHour));
            choice.setValue(date.getHour() >= 12 ? "pm" : "am");
            choice.setDisable(isReadOnly);
        }

        cmbHour.setDisable(isReadOnly);
        cmbMin.setValue(String.format("%02d", date.getMinute()));
        cmbMin.setDisable(isReadOnly);
        datePicker.setValue(date.toLocalDate());
        datePicker.setDisable(isReadOnly);
    }

    /**
     * fills the date fields with all necessary options to choose a start/end date and time
     */
    private void initializeDateFields() {
        ChoiceBox[] meridianPickers = {choiceEnd, choiceStart};
        for (ChoiceBox<String> choice : meridianPickers)
            if (is24Hour) {
                choice.setDisable(true);
                choice.setVisible(false);
            } else {
                choice.getItems().addAll("am", "pm");
                choice.getSelectionModel().select("am");
                choice.setDisable(isReadOnly);
            }


        ComboBox[] cmbHours = {cmbStartHour, cmbEndHour};
        for (ComboBox cmbHour : cmbHours) {
            ObservableList<String> options = cmbHour.getItems();

            if (is24Hour)
                for (int i = 0; i < 24; i++)
                    options.add(String.format("%02d", i));
            else
                for (int h = 0; h < 12; h++)
                    options.add(Integer.toString(h != 0 ? h : 12));

            cmbHour.getSelectionModel().select("12");
        }

        ComboBox[] cmbMinutes = {cmbStartMinute, cmbEndMinute};
        for (ComboBox cmbMinute : cmbMinutes) {
            ObservableList<String> options = cmbMinute.getItems();

            for (int i = 0; i < 60; i++)
                options.add(String.format("%02d", i));

            cmbMinute.getSelectionModel().select("00");
        }

        DatePicker[] datePickers = {pickerStart, pickerEnd};
        for (DatePicker datePicker : datePickers)
            datePicker.setValue(LocalDate.now());
    }

    /**
     * lambda1: consume an exception and result set and allow for DRY resource cleanup
     * <p>
     * creates a map of User Id to User object for easy lookup
     */
    private void loadUsers() {
        executeQuery("SELECT User_ID, User_Name FROM users", (ex, rs) -> {
            if (ex != null)
                return;

            try {
                while (rs.next()) {
                    User user = new User(rs.getLong(1), rs.getString(2));
                    users.put(user.getID(), user);
                }
            } catch (SQLException exception) {
                printSQLException(exception);
            }
        });

        cmbUser.getItems().addAll(users.values());
    }

    /**
     * parses a LocalDateTime out of the fields that make up the date/time info
     *
     * @param datePicker     a date picker for the date
     * @param hourPicker     a ComboBox for the hour
     * @param minutePicker   a ComboBox for the minute
     * @param meridianPicker a ChoiceBox for am/pm
     * @return the LocalDateTime contained by the various form elements
     */
    private LocalDateTime parseDateTime(DatePicker datePicker,
                                        ComboBox<String> hourPicker,
                                        ComboBox<String> minutePicker,
                                        ChoiceBox<String> meridianPicker) {
        int hour = hourPicker.getSelectionModel().getSelectedIndex();
        if (!is24Hour)
            hour += (meridianPicker.getSelectionModel().getSelectedItem().equals("am") ? 0 : 12);

        return datePicker.getValue().atTime(hour, minutePicker.getSelectionModel().getSelectedIndex())
                .atZone(ZoneOffset.systemDefault()).toLocalDateTime();
    }
}
