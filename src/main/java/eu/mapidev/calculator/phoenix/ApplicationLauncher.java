package eu.mapidev.calculator.phoenix;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ApplicationLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        ClassLoader classLoader = ApplicationLauncher.class.getClassLoader();

        primaryStage.setTitle("Phoenix Calculator");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Image icon = new Image(classLoader.getResourceAsStream("img/icon.png"));
        primaryStage.getIcons().add(icon);

        FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);

        MainController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        Thread.sleep(2000);
    }

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("javafx.preloader", ApplicationLoader.class.getCanonicalName());
        launch();
    }
}