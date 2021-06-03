package controller.detail;

import controller.BaseController;
import javafx.scene.control.Alert;
import model.base.BaseModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.base.ValidateException;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class DetailFrame<T extends BaseModel> extends BaseController implements Initializable {
    @FXML
    protected TextField tfid;

    @FXML
    private ButtonBar buttonBar;

    private Stage stage;
    private final FrameType frameType;
    private final String title;

    protected T row;
    protected boolean isReadOnly;
    protected Function<T, Boolean> function;

    public DetailFrame(String title, FrameType frameType, T row, Function<T, Boolean> function) {
        this.title = title;
        this.isReadOnly = frameType == FrameType.Read;
        this.frameType = frameType;
        this.row = row;
        this.function = function;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (row.getID() != 0)
            tfid.setText(Long.toString(row.getID()));

        tfid.setDisable(true);

        if (frameType != FrameType.Create) {
            if (frameType == FrameType.Read)
                buttonBar.setVisible(false);

            setFields();
            setTextFields();
        }
    }

    /**
     * Opens a frame to create/view/update a row
     *
     * @return this
     */
    public DetailFrame<T> open() {
        openFrame();
        return this;
    }

    /**
     * function call
     *
     * @param row the row that is to be saved/updated
     * @return whether the window can close
     */
    private boolean runFunction(T row) {
        if (function != null) {
            if (function.apply(row)) {
                function = null;
                return true;
            } else
                return false;
        }

        return true;
    }

    /**
     * called when the cancel button is clicked or any time the form must be closed
     */
    private void closeClicked() {
        if (stage != null)
            stage.hide();

        stage = null;
    }

    /**
     * close window
     * @param event an action event from JavaFX when the button is clicked
     */
    @FXML
    private void closeClicked(ActionEvent event) {
        runFunction(null);
        closeClicked();
    }

    /**
     * save changes
     */
    @FXML
    private void saveClicked() {
        try {
            for (Node button : buttonBar.getButtons())
                button.setDisable(true);

            applyStringToRow();
            applyAllToRow();
            row.validate();

            if (runFunction(row))
                closeClicked();
        } catch (ValidateException err) {
            showMessageBox(resourceBundle.getString("error.defaultTitle"), err.getMessage(), Alert.AlertType.ERROR);
        }

        buttonBar.getButtons().forEach(btn -> btn.setDisable(false));
    }

    /**
     * apply to all fields in row
     */
    abstract protected void applyAllToRow();

    /**
     * applies the values from the row to the form so that an existing row can be updated. when creating a new
     * row, this method isn't called and the form values are left in their default state.
     */
    protected abstract void setFields();

    /**
     * Allows subclasses to define the path to their FXML files for dynamic and polymorphic forms.
     *
     * @return the resource url for the form FXML
     */
    protected abstract String getResourceURL();

    /**
     * allows for dynamic setting of the title of the form window based on the current action
     *
     * @return the title to be set for the form window
     */
    private String getTitle() {
        return title;
    }

    /**
     * @return the width of the form window
     */
    protected abstract double getWidth();

    /**
     * @return the height of the form window
     */
    protected abstract double getHeight();

    /**
     * lambda1: ensures the callback is always called
     * <p>
     * Opens a new window with the correct form for the controller
     */
    private void openFrame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(getResourceURL()), resourceBundle);
            loader.setController(this);
            Scene scene = new Scene(loader.load(), getWidth(), getHeight());
            stage = new Stage();
            stage.setOnHidden(ev -> closeClicked(null));
            stage.setScene(scene);
            stage.setTitle(getTitle());
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception ex) {
            System.out.println(ex);
            ex.printStackTrace();
            closeClicked(null);
        }
    }

    /**
     * set text fields
     *
     * @see DetailFrame#loopStrFields(BiConsumer)
     */
    private void setTextFields() {
        loopStrFields((tf, rowField) -> {
            try {
                String data = (String) rowField.get(row);
                tf.setText(data);
                tf.setDisable(isReadOnly);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * apply string
     * @see DetailFrame#loopStrFields(BiConsumer)
     */
    private void applyStringToRow() {
        loopStrFields((tf, rowField) -> {
            try {
                rowField.set(row, tf.getText().trim());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * get String fields
     * @return a list of string Fields of the row
     */
    private List<Field> getStringFields() {
        final List<Field> output = new ArrayList<>();

        for (Field declaredField : row.getClass().getDeclaredFields())
            if (declaredField.getType() == String.class)
                output.add(declaredField);

        return output;
    }

    /**
     * loop string fields
     *
     * @param consumer a lambda expression for processing the TextField and its matching member in the row
     * @see DetailFrame#applyStringToRow()
     * @see DetailFrame#setTextFields()
     */
    private void loopStrFields(BiConsumer<TextField, Field> consumer) {
        for (final Field field : getStringFields()) {
            try {
                field.setAccessible(true);
                Field textFieldField = getClass().getDeclaredField(String.format("tf%s", field.getName()));
                textFieldField.setAccessible(true);
                consumer.accept((TextField) textFieldField.get(this), field);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    protected long getRowID(BaseModel row) {
        return Optional.ofNullable(row).map(BaseModel::getID).orElse(0L);
    }
}
