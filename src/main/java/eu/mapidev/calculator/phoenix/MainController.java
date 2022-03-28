package eu.mapidev.calculator.phoenix;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private GridPane root;
    @FXML
    private Label lblResult;
    @FXML
    private Label lblMemory;

    private Stage primaryStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.setOnMousePressed(pressEvent -> {
            root.setOnMouseDragged(dragEvent -> {
                if (primaryStage == null)
                    return;

                primaryStage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                primaryStage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void clean(ActionEvent actionEvent) {
        lblMemory.setText("0");
        lblResult.setText("0");
    }

    @FXML
    public void concatNumber(ActionEvent actionEvent) {
        String buttonTextNumber = ((Button) actionEvent.getSource()).getText();
        if ("0".equals(lblResult.getText()))
            lblResult.setText(buttonTextNumber);
        else
            lblResult.setText(lblResult.getText() + buttonTextNumber);
    }
}