package model.db;

import model.base.BaseModel;
import model.base.Model;
import model.base.ViewReport;

import java.util.ArrayList;
import java.util.List;

public class Customer extends BaseModel implements Model<Customer>, ViewReport {
    /**
     * name of customer
     */
    private String name;

    /**
     * address of customer
     */
    private final String address;

    /**
     * postal code of customer
     */
    private final String postalCode;

    /**
     * phone of customer
     */
    private final String phone;

    /**
     * division id of customer
     */
    private long divisionID;

    /**
     * Constructor
     * @param id - id of customer
     * @param name - name of customer
     * @param address - address of customer
     * @param postalCode - postal code of customer
     * @param phone - phone of customer
     * @param divisionID - division id of customer
     */
    public Customer(long id, String name, String address, String postalCode, String phone, long divisionID) {
        super(id);
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionID = divisionID;
    }

    /**
     * get name of customer
     * @return - name of customer
     */
    public String getName() {
        return name;
    }

    /**
     * set name of customer
     * @param name - new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get division id of customer
     * @return division id of customer
     */
    public long getDivisionID() {
        return divisionID;
    }

    /**
     * set division id of customer
     * @param divisionID - division id of customer
     */
    public void setDivisionID(long divisionID) {
        this.divisionID = divisionID;
    }

    /**
     * @see Model#toList()
     */
    @Override
    public List<Object> toList() {
        return new ArrayList<>(List.of(name, address, postalCode, phone, divisionID));
    }

    /**
     * @see Model#copy()
     */
    @Override
    public Customer copy() {
        return new Customer(ID, name, address, postalCode, phone, divisionID);
    }

    /**
     * toString method
     * @return the name of the customer
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
        return String.format("\t %d\t %s\t %s\t %s\t %s\n", ID, name, address, postalCode, phone);
    }

    /**
     * to be overridden by subclasses that have non-String fields to validate
     */
    @Override
    protected void customValidate() {
    }
}
