package model.db;

import model.base.BaseModel;
import model.base.Model;
import model.base.ValidateException;
import model.base.ViewReport;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

public class Appointment extends BaseModel implements Model<Appointment>, ViewReport {
    private String title;
    private final String description;
    private final String location;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private long customerID;
    private long userID;
    private long contactID;

    public Appointment(long ID,
                       String title,
                       String description,
                       String location,
                       String type,
                       LocalDateTime start,
                       LocalDateTime end,
                       long customerID,
                       long userID,
                       long contactID) {
        super(ID);
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
    }

    /**
     * @see Model#copy()
     */
    @Override
    public Appointment copy() {
        return new Appointment(ID, title, description, location, type, start, end, customerID, userID, contactID);
    }

    /**
     * @see Model#toList()
     */
    @Override
    public List<Object> toList() {
        return new ArrayList<>(List.of(
                title,
                description,
                location,
                type,
                getStartDateUTCString(),
                getEndDateUTCString(),
                customerID,
                userID,
                contactID));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type.trim();
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public long getCustomerID() {
        return customerID;
    }

    public void setCustomerID(long customerID) {
        this.customerID = customerID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getContactID() {
        return contactID;
    }

    public void setContactID(long contactID) {
        this.contactID = contactID;
    }

    /**
     * @return string of start date
     */
    public String getStartDateUTCString() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .format(start.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")));
    }

    /**
     * @return string of end date
     */
    public String getEndDateUTCString() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .format(end.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")));
    }

    /**
     * @return the start date formatted for display in the table
     */
    public String getStartDateLocaleString() {
        return start.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale));
    }

    /**
     * @return the end date formatted for display in the table
     */
    public String getEndDateLocaleString() {
        return end.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale));
    }

    /**
     * @return the start time in the local user's time zone
     */
    public ZonedDateTime getLocalStart() {
        return start.atZone(ZoneId.systemDefault());
    }

    /**
     * @return the end time in the local user's time zone
     */
    public ZonedDateTime getLocalEnd() {
        return end.atZone(ZoneId.systemDefault());
    }

    /**
     * @see BaseModel#customValidate()
     */
    @Override
    protected void customValidate() throws ValidateException {
        final ZonedDateTime startTimeEST = start.atZone(ZoneId.of("US/Eastern"));
        final ZonedDateTime endTimeEST = end.atZone(ZoneId.of("US/Eastern"));

        validateTimeRange(startTimeEST, resourceBundle.getString("appointment.start"));
        validateTimeRange(endTimeEST, resourceBundle.getString("appointment.end"));

        if (start.compareTo(end) > 0)
            throw new ValidateException(resourceBundle.getString("error.startAfterEnd"));

        if (!startTimeEST.toLocalDate().equals(endTimeEST.toLocalDate()))
            throw new ValidateException(resourceBundle.getString("error.notSameDay"));
    }

    /**
     * validates that the row's hours fall within business hours
     *
     * @param date the start or end date and time
     * @param name the name of the field
     * @throws ValidateException an error saying the value is out of range
     */
    private void validateTimeRange(ZonedDateTime date, String name) throws ValidateException {
        if (date.getHour() < 8 || date.getHour() > 22 || (date.getHour() == 22 && date.getMinute() != 0))
            throw new ValidateException(resourceBundle.getString("error.invalidDateRange").replace("%{field}", name));
    }

    /**
     * @see ViewReport#viewString()
     */
    @Override
    public String viewString() {
        String output = "";

        output += String.format("\t%s: %d\n", resourceBundle.getString("appointment.ID"), ID);
        output += String.format("\t%s: %s\n", resourceBundle.getString("appointment.title"), title);
        output += String.format("\t%s: %s\n", resourceBundle.getString("appointment.type"), type);
        output += String.format("\t%s: %s\n", resourceBundle.getString("appointment.description"), description);
        output += String.format("\t%s: %s\n", resourceBundle.getString("appointment.start"), getStartDateLocaleString());
        output += String.format("\t%s: %s\n", resourceBundle.getString("appointment.end"), getEndDateLocaleString());

        return output + "\n";
    }
}
