package controller.other;

import controller.BaseController;
import controller.list.ListAppointmentListTable;
import controller.list.ListCustomerListTable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainFrame extends BaseController implements Initializable {
    private final RunEvent runEvent = new RunEvent();
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tabCustomer;
    @FXML
    private Tab tabAppointment;

    private ListCustomerListTable listCustomerTableController;

    private boolean isCustomerTabInit = false;
    private boolean isAppointmentTabInit = false;

    /**
     * initialize
     * @see Initializable#initialize(URL, ResourceBundle)
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabPane.getSelectionModel().selectedItemProperty()
                .addListener(((observableValue, oldTab, newTab) -> loadData(newTab)));
        loadData(tabPane.getSelectionModel().getSelectedItem());
    }

    /**
     * is called whenever the tab on the main view changes, it makes sure that all the tabs are initialized
     * @param newTab the currently selected tab
     */
    private void loadData(Tab newTab) {
        if (newTab == tabCustomer)
            loadCustomer();
        else if (newTab == tabAppointment)
            loadAppointment();
    }

    /**
     * creates the customer table if it's not already initialized
     */
    private void loadCustomer() {
        if (isCustomerTabInit)
            return;

        isCustomerTabInit = true;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TableFrame.fxml"), resourceBundle);
        listCustomerTableController = new ListCustomerListTable(runEvent);
        loader.setController(listCustomerTableController);

        try {
            tabCustomer.setContent(loader.load());
        } catch (IOException ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }

    /**
     * creates the appointment table if it's not already initialized
     */
    private void loadAppointment() {
        if (isAppointmentTabInit)
            return;

        isAppointmentTabInit = true;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TableFrame.fxml"), resourceBundle);
        loader.setController(new ListAppointmentListTable(listCustomerTableController.getData(), runEvent));

        try {
            tabAppointment.setContent(loader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public enum Event {
        CustomerDeleted
    }
}
