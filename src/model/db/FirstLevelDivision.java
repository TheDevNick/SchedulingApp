package model.db;

import model.base.BaseModel;
import model.base.ViewReport;

public class FirstLevelDivision extends BaseModel implements ViewReport {
    /**
     * first level division
     */
    private final String division;
    /**
     * country id of devision
     */
    private final long countryID;

    /**
     * Constructor
     * @param id - id of first-level division
     * @param division - name of division
     * @param countryID - country id of division
     */
    public FirstLevelDivision(long id, String division, long countryID) {
        super(id);
        this.division = division;
        this.countryID = countryID;
    }

    /**
     * get name of first-level division
     * @return name of division
     */
    public String getDivision() {
        return division;
    }

    /**
     * get country id of division
     * @return country id of division
     */
    public long getCountryID() {
        return countryID;
    }

    /**
     * toString method
     * @return the name of the division
     */
    @Override
    public String toString() {
        return division;
    }

    /**
     * @see ViewReport#viewString()
     */
    @Override
    public String viewString() {
        return String.format("%d\t%s:\n", ID, division);
    }

    /**
     * to be overridden by subclasses that have non-String fields to validate
     */
    @Override
    protected void customValidate() {
    }
}
