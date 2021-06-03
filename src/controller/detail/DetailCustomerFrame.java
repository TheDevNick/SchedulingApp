package controller.detail;

import model.db.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.*;
import java.util.function.Function;

public class DetailCustomerFrame extends DetailFrame<Customer> implements Initializable {
    @FXML
    private TextField tfname;
    @FXML
    private TextField tfaddress;
    @FXML
    private TextField tfpostalCode;
    @FXML
    private TextField tfphone;
    @FXML
    private ComboBox<FirstLevelDivision> cmbDivision;
    @FXML
    private ComboBox<Country> cmbCountry;

    private final Map<Long, Country> countries;
    private final Map<Long, FirstLevelDivision> divisions;

    public DetailCustomerFrame(String title,
                               Map<Long, FirstLevelDivision> divisions,
                               Map<Long, Country> countries,
                               FrameType frameType,
                               Customer row,
                               Function<Customer, Boolean> function) {
        super(title, frameType, row, function);
        this.divisions = divisions;
        this.countries = countries;
    }

    /**
     * initialize
     *
     * @see Initializable#initialize(URL, ResourceBundle)
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbCountry.getItems().addAll(countries.values());

        ObservableList<Country> countries = cmbCountry.getItems();

        divisions.forEach((id, firstLevelDivision) -> {
            Country country = this.countries.get(firstLevelDivision.getCountryID());
            if (!countries.contains(country))
                countries.add(country);
        });

        countries.sort(Comparator.comparing(Country::getCountry));
        cmbDivision.setDisable(true);
        super.initialize(url, resourceBundle);
    }

    /**
     * @see DetailFrame#setFields()
     */
    @Override
    protected void setFields() {
        FirstLevelDivision firstLevelDivision = divisions.get(row.getDivisionID());
        cmbCountry.getSelectionModel().select(countries.get(firstLevelDivision.getCountryID()));
        cmbCountry.setDisable(isReadOnly);
        loadDivision(null);
        cmbDivision.getSelectionModel().select(firstLevelDivision);
    }

    /**
     * @see DetailFrame#getResourceURL()
     */
    @Override
    protected String getResourceURL() {
        return "/view/CustomerFrame.fxml";
    }

    /**
     * @see DetailFrame#applyAllToRow()
     */
    @Override
    protected void applyAllToRow() {
        row.setDivisionID(getRowID(cmbDivision.getValue()));
    }

    /**
     * @see DetailFrame#getHeight()
     */
    @Override
    protected double getHeight() {
        return 768;
    }

    /**
     * @see DetailFrame#getWidth()
     */
    @Override
    protected double getWidth() {
        return 1024;
    }

    /**
     * load division data
     * @param event JavaFX action event
     */
    @FXML
    private void loadDivision(ActionEvent event) {
        Country country = cmbCountry.getValue();
        ObservableList<FirstLevelDivision> firstLevelDivisions = cmbDivision.getItems();
        firstLevelDivisions.clear();

        divisions.forEach((key, firstLevelDivision) -> {
            if (firstLevelDivision.getCountryID() == country.getID())
                firstLevelDivisions.add(firstLevelDivision);
        });

        cmbDivision.setDisable(isReadOnly || cmbDivision.getItems().isEmpty());
        firstLevelDivisions.sort(Comparator.comparing(FirstLevelDivision::getDivision));
    }

}
