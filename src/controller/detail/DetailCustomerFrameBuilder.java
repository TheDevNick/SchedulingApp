package controller.detail;

import model.db.Country;
import model.db.Customer;
import model.db.FirstLevelDivision;
import model.base.BaseModel;

import java.util.Map;
import java.util.function.Function;

public final class DetailCustomerFrameBuilder extends DetailFrameBuilder<Customer, DetailCustomerFrame> {
    private Map<Long, FirstLevelDivision> divisions;
    private Map<Long, Country> countries;

    public DetailCustomerFrameBuilder(Class<Customer> modelClass) {
        super(modelClass);
    }

    /**
     * @see DetailFrameBuilder#getInstance(FrameType, BaseModel, Function)
     */
    @Override
    public DetailCustomerFrame getInstance(FrameType frameType, Customer row, Function<Customer, Boolean> function) {
        return new DetailCustomerFrame(getTitle(frameType), divisions, countries, frameType, row, function);
    }

    /**
     * set countries
     * @param countries a map of countryId to country models
     */
    public void setCountries(Map<Long, Country> countries) {
        this.countries = countries;
    }

    /**
     * set divisions
     *
     * @param divisions a map of divisionId to division models
     */
    public void setDivisions(Map<Long, FirstLevelDivision> divisions) {
        this.divisions = divisions;
    }

}
