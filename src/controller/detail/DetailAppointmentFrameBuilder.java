package controller.detail;

import model.db.Appointment;
import model.db.Contact;
import model.db.Customer;
import model.base.BaseModel;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DetailAppointmentFrameBuilder extends DetailFrameBuilder<Appointment, DetailAppointmentFrame> {
    private Map<Long, Contact> contacts;
    private List<Customer> customerList;

    public DetailAppointmentFrameBuilder(Class<Appointment> modelClass) {
        super(modelClass);
    }

    /**
     * @see DetailFrameBuilder#getInstance(FrameType, BaseModel, Function)
     */
    @Override
    public DetailAppointmentFrame getInstance(FrameType frameType, Appointment row, Function<Appointment, Boolean> function) {
        return new DetailAppointmentFrame(getTitle(frameType), contacts, customerList, frameType, row, function);
    }

    /**
     * set contacts
     * @param contacts a map of contactId to contact models
     */
    public void setContacts(Map<Long, Contact> contacts) {
        this.contacts = contacts;
    }

    /**
     * set customers
     * @param customerList a list of all customers
     */
    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }
}
