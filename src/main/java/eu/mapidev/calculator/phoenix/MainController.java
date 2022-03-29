package eu.mapidev.calculator.phoenix;

import eu.mapidev.calculator.phoenix.utils.ResultUtils;
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

    public static final String DEFAULT_RESULT = "0";

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
    public void concatNumber(ActionEvent actionEvent) {
        String buttonTextNumber = ((Button) actionEvent.getSource()).getText();
        String result = lblResult.getText();
        lblResult.setText(DEFAULT_RESULT.equals(result) ? buttonTextNumber : result + buttonTextNumber);
    }

    @FXML
    public void clean(ActionEvent actionEvent) {
        lblMemory.setText(DEFAULT_RESULT);
        lblResult.setText(DEFAULT_RESULT);
    }

    @FXML
    public void changeSign(ActionEvent actionEvent) {
        String result = lblResult.getText();
        if (DEFAULT_RESULT.equals(result))
            return;

        lblResult.setText(result.startsWith("-") ? result.substring(1) : "-" + result);
    }

    @FXML
    public void addComma(ActionEvent actionEvent) {
        String result = lblResult.getText();
        if (result.contains(ResultUtils.COMMA))
            return;

        lblResult.setText(result + ResultUtils.COMMA);
    }
}