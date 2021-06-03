package model.db;

import model.base.BaseModel;
import model.base.ViewReport;

public class Contact extends BaseModel implements ViewReport {
    /**
     * name of contact
     */
    final private String name;
    /**
     * name of email
     */
    final private String email;

    /**
     * Constructor
     * @param id - id of contact
     * @param name - name of contact
     * @param email - email of contact
     */
    public Contact(long id, String name, String email) {
        super(id);
        this.name = name;
        this.email = email;
    }

    /**
     * get name of contact
     * @return - name
     */
    public String getName() {
        return name;
    }

    /**
     * get email of contact
     * @return - email
     */
    public String getEmail() {
        return email;
    }

    /**
     * toString method
     * @return the name of the contact
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * @see ViewReport#viewString()
     */
    @Override
    public String viewString() {
        return String.format("%d\t%s\t%s\n", ID, name, email);
    }

    @Override
    protected void customValidate() {
    }
}
