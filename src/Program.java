import controller.BaseController;
import controller.ViewController;
import model.base.BaseModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Locale;

public class Program extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BaseController.setLocaleAndBundle();
        BaseModel.resourceBundle = BaseController.getResourceBundle();
        BaseModel.locale = BaseController.getLocale();
        Locale.setDefault(BaseController.getLocale());
        final Scene scene = new Scene(new StackPane());

        ViewController viewController = new ViewController(scene, primaryStage);
        viewController.showLoginView();

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
