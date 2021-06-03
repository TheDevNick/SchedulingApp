package controller.other;

import controller.BaseController;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * controller to filter the appointment table by month or week and year
 */
public class FilterFrame extends BaseController implements Initializable {
    @FXML
    private final ToggleGroup tglGroup = new ToggleGroup();
    @FXML
    private ComboBox<Integer> cmbYear;
    @FXML
    private ComboBox<CmbItem> cmbValues;
    @FXML
    private RadioButton btnMonth;
    @FXML
    private RadioButton btnWeek;
    @FXML
    private Label lbCmb;

    private Stage stage;
    private Consumer<FilterItem> consumer;
    private String field;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setYear();
        tglGroup.selectedToggleProperty().addListener(this::updateToggleItem);
        cmbYear.getSelectionModel().selectedItemProperty().addListener(this::yearChanged);
        tglGroup.getToggles().addAll(btnMonth, btnWeek);
        tglGroup.selectToggle(btnMonth);
    }

    private void yearChanged(Observable observable) {
        updateToggleItem(null, null, tglGroup.getSelectedToggle());
    }

    /**
     * used to populate the month/week ComboBox with the appropriate values for the selected year
     *
     * @param observable not used
     * @param oldValue   not used
     * @param newValue   the radio button that has been selected
     */
    private void updateToggleItem(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
        String bundleProp;
        if (newValue == btnMonth) {
            bundleProp = "month";
            setMonth();
        } else {
            bundleProp = "week";
            setWeek();
        }
        field = bundleProp.toUpperCase();
        lbCmb.setText(resourceBundle.getString(String.format("form.%s", bundleProp)));
        cmbValues.getSelectionModel().selectFirst();
    }

    /**
     * set year
     */
    private void setYear() {
        executeQuery("SElECT DISTINCT YEAR(`Start`) FROM appointments ORDER BY YEAR(`Start`)", (e, rs) -> {
            if (e != null)
                return;

            ObservableList<Integer> years = cmbYear.getItems();
            try {
                while (rs.next())
                    years.add(rs.getInt(1));
            } catch (SQLException exception) {
                printSQLException(exception);
            }
        });

        cmbYear.getSelectionModel().selectFirst();
    }

    /**
     * set weeks
     */
    private void setWeek() {
        List<Object> arguments = List.of(cmbYear.getValue());
        ObservableList<CmbItem> items = cmbValues.getItems();
        items.clear();

        executeQuery("SELECT DISTINCT WEEK(`Start`) " +
                "FROM appointments " +
                "WHERE YEAR(`Start`) = ? " +
                "ORDER BY WEEK(`Start`)", arguments, (e, rs) -> {
            if (e != null)
                return;

            try {
                while (rs.next()) {
                    int value = rs.getInt(1);
                    items.add(new CmbItem(Integer.toString(value + 1), value));
                }
            } catch (SQLException exception) {
                printSQLException(exception);
            }
        });
    }

    /**
     * set month
     */
    private void setMonth() {
        List<Object> arguments = List.of(cmbYear.getValue());
        ObservableList<CmbItem> items = cmbValues.getItems();
        items.clear();

        executeQuery("SELECT DISTINCT MONTH(`Start`) " +
                "FROM appointments " +
                "WHERE YEAR(`Start`) = ? " +
                "ORDER BY MONTH(`Start`)", arguments, (e, rs) -> {
            if (e != null)
                return;

            try {
                while (rs.next()) {
                    final int month = rs.getInt(1);
                    items.add(new CmbItem(resourceBundle.getString(String.format("month.%d", month)), month));
                }
            } catch (SQLException exception) {
                printSQLException(exception);
            }
        });
    }

    /**
     * accept consumer
     *
     * @param values the values to filter by
     */
    private void callCallback(FilterItem values) {
        if (consumer != null) {
            consumer.accept(values);
            consumer = null;
        }
    }

    /**
     * save filter
     * @param event JavaFX action event
     */
    @FXML
    private void saveClicked(ActionEvent event) {
        int year = cmbYear.getValue();
        int fieldValue = cmbValues.getValue().id;
        FilterItem fields = new FilterItem(year, field, fieldValue);
        callCallback(fields);
        closeClicked(null);
    }

    /**
     * clear filter
     * @param event JavaFX action event
     */
    @FXML
    private void clearClicked(ActionEvent event) {
        callCallback(null);
        closeClicked(null);
        tglGroup.getToggles().clear();
    }

    /**
     * close filter
     * @param event JavaFX action event
     */
    @FXML
    private void closeClicked(ActionEvent event) {
        if (stage != null) stage.close();
        stage = null;
    }

    /**
     * open filter window
     * @param consumer consumer
     */
    public void openFilterWindow(Consumer<FilterItem> consumer) {
        this.consumer = consumer;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FilterFrame.fxml"), resourceBundle);
            loader.setController(this);
            Scene scene = new Scene(loader.load(), 400, 200);
            stage = new Stage();

            stage.setOnHidden(ev -> closeClicked(null));
            stage.setScene(scene);
            stage.setTitle(resourceBundle.getString("filter.windowTitle"));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            closeClicked(null);
        }
    }

    public static class FilterItem {
        final public int year;
        final public String field;
        final public int fieldValue;

        public FilterItem(int year, String field, int fieldValue) {
            this.year = year;
            this.field = field;
            this.fieldValue = fieldValue;
        }
    }

    private static class CmbItem {
        final public int id;
        final private String value;

        public CmbItem(String value, int id) {
            this.value = value;
            this.id = id;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
