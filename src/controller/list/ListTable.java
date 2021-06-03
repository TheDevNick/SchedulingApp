package controller.list;

import controller.BaseController;
import controller.other.RunEvent;
import controller.detail.*;
import model.base.Model;
import model.base.BaseModel;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.function.Function;

public abstract class ListTable<T extends BaseModel & Model<T>> extends BaseController implements Initializable {
    @FXML
    protected TableView<T> tblView;
    @FXML
    protected Button btnFilter;
    @FXML
    private Button btnDelete;

    final protected DetailFrameBuilder detailFrameBuilder;
    final protected RunEvent runEvent;
    protected DetailFrame detailFrameController;

    public ListTable(DetailFrameBuilder detailFrameBuilder, RunEvent runEvent) {
        this.detailFrameBuilder = detailFrameBuilder;
        this.runEvent = runEvent;
    }

    /**
     * adds a filter to the table, only used by the AppointmentTable
     */
    @FXML
    protected void addFilter() {
    }

    /**
     * lambda1: opens the form and registers a callback to be called with the completed row
     * <p>
     * opens the form with a blank row and saves it into the database
     */
    @FXML
    private void addRow() {
        if (detailFrameController == null) {
            openForm(FrameType.Create, getNewRow(), (newRow) -> {
                final boolean rowHandled = newRow == null || addToDatabase(newRow);
                if (rowHandled) {
                    if (newRow != null && newRow.getID() != 0) {
                        tblView.getItems().add(newRow);
                    }

                    refreshTbl();
                }
                return rowHandled;
            });
        }
    }

    /**
     * view row
     */
    @FXML
    private void viewRow() {
        final T selectedRow = getSelectedRow();
        if (selectedRow != null && detailFrameController == null) {
            openForm(FrameType.Read, selectedRow, (row) -> {
                refreshTbl();
                return true;
            });
        }
    }

    /**
     * edit row
     * <p>
     * opens the form with the given row and executes a SQL statement to update a row in the database
     */
    @FXML
    private void editRow() {
        T selected = getSelectedRow();
        if (selected != null && detailFrameController == null) {
            openForm(FrameType.Update, selected.copy(), (updateRow) -> {
                boolean rowHandler = updateRow == null || updateDB(updateRow);

                if (rowHandler)
                    refreshTbl();

                return rowHandler;
            });
        }
    }

    /**
     * initialize
     *
     * @see Initializable#initialize(URL, ResourceBundle)
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnFilter.setDisable(true);
        btnFilter.setVisible(false);

        TableColumn<T, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getID()).asObject());
        tblView.getColumns().add(idColumn);

        addColumns();
        loadData();
        tblView.refresh();
    }

    @FXML
    protected void deleteRow() {
        final T rowToDelete = getSelectedRow();
        if (rowToDelete != null) {
            btnDelete.setDisable(true);
            String message = getDeletedStatus(rowToDelete);
            deleteFromDB(rowToDelete);

            if (rowToDelete.getID() == 0) {
                tblView.getItems().remove(rowToDelete);
                tblView.refresh();
                showMessageBox(resourceBundle.getString("row.deleted.title"), message, Alert.AlertType.INFORMATION);
                runEvent();
            }

            btnDelete.setDisable(false);
        }
    }

    public ObservableList<T> getData() {
        return tblView.getItems();
    }

    /**
     * get string column names
     * @param className    the class for T
     * @param fieldName the name of the instance field
     * @return the TableColumn for the row
     */
    protected TableColumn<T, String> getTableColumn(Class<T> className, String fieldName) {
        try {
            Field field = className.getDeclaredField(fieldName);
            field.setAccessible(true);
            String key = String.format("%s.%s", className.getSimpleName().toLowerCase(), field.getName());
            TableColumn<T, String> column = new TableColumn<>(resourceBundle.getString(key));

            column.setCellValueFactory(col -> {
                try {
                    return new SimpleStringProperty((String) field.get(col.getValue()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                return null;
            });

            return column;
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * opens a form in the proper mode with the given row
     *
     * @param frameType     the mode for the form
     * @param row   the row to display in the form
     * @param function the callback to consume the row after editing has finished
     */
    private void openForm(FrameType frameType, T row, Function<T, Boolean> function) {
        detailFrameController = detailFrameBuilder.getInstance(frameType, row, function);
        detailFrameController.open();
    }

    /**
     * refresh table
     */
    private void refreshTbl() {
        tblView.refresh();
        detailFrameController = null;
    }

    /**
     * add to database new row
     *
     * @param row the row to insert
     * @return whether the form can close or not
     */
    private boolean addToDatabase(T row) {
        final boolean updatable = canUpdate(row);
        if (updatable) {
            final List<Object> arguments = row.toList();
            arguments.add(userID);
            arguments.add(userID);

            executeInsert(getInsertQuery(), arguments, (ex, newId) -> {
                if (ex != null)
                    printSQLException(ex);

                if (newId != null)
                    row.setID(newId);
            });
        }

        return updatable;
    }

    /**
     * @return a string with SQL insert query for a row
     */
    protected abstract String getInsertQuery();

    /**
     * @return a blank row to use to hold the data for a new row before it is saved to the databases
     */
    protected abstract T getNewRow();

    private T getSelectedRow() {
        return tblView.getSelectionModel().getSelectedItem();
    }

    /**
     * update DB with row
     * @param row the row to update
     * @return whether the form can close or not
     */
    protected boolean updateDB(T row) {
        final boolean updatable = canUpdate(row);
        if (updatable) {
            List<Object> arguments = row.toList();
            arguments.add(userID);
            arguments.add(row.getID());
            executeUpdate(getUpdateQuery(), arguments, (ex, updateCount) -> {
                if (ex != null)
                    printSQLException(ex);

                if (updateCount == 1)
                    getSelectedRow().apply(row);
            });
        }
        return updatable;
    }

    /**
     * takes a list of arguments and converts it into a list of objects
     *
     * @param values the values to include in the list
     * @return a list of the values
     */
    protected List<Object> toArray(Object... values) {
        final List<Object> output = new ArrayList<>();

        if (values != null)
            Collections.addAll(output, values);
        else
            output.add(null);

        return output;
    }

    /**
     * Delete row
     * @param row the row delete
     */
    protected void deleteFromDB(T row) {
        if (deleteCustomer(row)) {
            executeUpdate(getDeleteQuery(), toArray(row.getID()), (ex, updates) -> {
                if (ex != null)
                    printSQLException(ex);

                if (updates == 1)
                    row.setID(0);
            });
        }
    }

    /**
     * load data
     */
    protected abstract void loadData();

    /**
     * delete appointment for delete customer
     * @param row the row whose dependencies need to be deleted
     * @return whether the dependent rows could be deleted successfully
     */
    protected abstract boolean deleteCustomer(T row);

    /**
     * @return a SQL statement that can delete a row from a table
     */
    protected abstract String getDeleteQuery();

    protected abstract void runEvent();

    protected abstract String getDeletedStatus(T row);

    /**
     * adds columns to the table that match the shape of the generic T
     */
    protected abstract void addColumns();

    /**
     * performs SQL validations on the row to ensure it is valid
     *
     * @param row the row to update
     * @return whether the row can be updated
     */
    protected abstract boolean canUpdate(T row);

    /**
     * @return a string that contains a SQL statement to update a row
     */
    protected abstract String getUpdateQuery();

}
