package controller;

import controller.other.LoginFrame;
import controller.other.MainFrame;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

/**
 * manages the changing the view from the log in screen to the main view
 */
public final class ViewController extends BaseController {
    private final Scene scene;
    private final Stage primaryStage;

    public ViewController(final Scene scene, final Stage primaryStage) {
        this.scene = scene;
        this.primaryStage = primaryStage;
    }

    /**
     * show login view
     *
     * @throws Exception any exception within the scene building
     */
    public void showLoginView() throws Exception {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginFrame.fxml"), resourceBundle);
        scene.setRoot(loader.load());
        loader.<LoginFrame>getController().setViewController(this);
        primaryStage.setTitle(resourceBundle.getString("app.title"));
        primaryStage.setWidth(300);
        primaryStage.setHeight(150);
    }

    /**
     * show main view
     */
    public void showMainView() {
        try {
            primaryStage.hide();
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainFrame.fxml"), resourceBundle);
            scene.setRoot(loader.load());
            final MainFrame mainFrameController = loader.getController();
            mainFrameController.setViewController(this);
            Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
            primaryStage.setWidth(size.width);
            primaryStage.setHeight(size.height - 50);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
