package model.db;

import model.base.BaseModel;

public class Country extends BaseModel {
    /**
     * country name
     */
    private final String country;

    /**
     * Constructor
     * @param id - id of country
     * @param country - name of country
     */
    public Country(int id, String country) {
        super(id);
        this.country = country;
    }

    /**
     * get country name
     * @return - name of country
     */
    public String getCountry() {
        return country;
    }

    /**
     * toString method
     * @return the name of the country
     */
    @Override
    public String toString() {
        return country;
    }

    /**
     * to be overridden by subclasses that have non-String fields to validate
     */
    @Override
    protected void customValidate() {

    }
}
