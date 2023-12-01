package eu.mapidev.calculator.phoenix;

import eu.mapidev.calculator.phoenix.logic.CalculatorEngine;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    private CalculatorEngine calculatorEngine;


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

        root.setOnKeyPressed(keyEvent -> {
            root.getChildren().stream()
                    .filter(node -> node instanceof Button)
                    .map(node -> (Button) node)
                    .filter(button -> button.getText().equals(getBindingButtonTextByEvent(keyEvent)))
                    .findFirst()
                    .ifPresent(this::buttonFireWithClickAnimation);
        });

        calculatorEngine = new CalculatorEngine();
        calculatorEngine.setOnResultChanged(lblResult::setText);
        calculatorEngine.setOnMemoryChanged(lblMemory::setText);
    }

    private String getBindingButtonTextByEvent(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case ENTER:
            case EQUALS:
                return "=";
            case NUMBER_SIGN:
                return "+/-";
            case COMMA:
            case SEPARATOR:
            case DECIMAL:
                return ",";
            case DELETE:
                return "C";
            case PLUS:
            case ADD:
                return "+";
            case MINUS:
            case SUBTRACT:
                return "-";
            case MULTIPLY:
                return "x";
            case DIVIDE:
                return "/";
            case NUMPAD0:
            case NUMPAD1:
            case NUMPAD2:
            case NUMPAD3:
            case NUMPAD4:
            case NUMPAD5:
            case NUMPAD6:
            case NUMPAD7:
            case NUMPAD8:
            case NUMPAD9:
                String name = keyEvent.getCode().getName();
                return name.substring(name.length() - 1);
            case DIGIT0:
            case DIGIT1:
            case DIGIT2:
            case DIGIT3:
            case DIGIT4:
            case DIGIT5:
            case DIGIT6:
            case DIGIT7:
            case DIGIT8:
            case DIGIT9:
                return keyEvent.getCode().getName();
        }

        return null;
    }

    private void buttonFireWithClickAnimation(Button button) {
        button.arm();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.01));
        pause.setOnFinished(event -> button.disarm());
        pause.play();
        button.fire();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void handleNumberPressed(ActionEvent actionEvent) {
        String buttonTextNumber = ((Button) actionEvent.getSource()).getText();
        calculatorEngine.concatNumber(buttonTextNumber);
    }

    @FXML
    public void handlePercentPressed(ActionEvent actionEvent) {
        calculatorEngine.doPercent();
    }

    @FXML
    public void handleReset(ActionEvent actionEvent) {
        calculatorEngine.reset();
    }

    @FXML
    public void handleChangeSign(ActionEvent actionEvent) {
        calculatorEngine.changeSign();
    }

    @FXML
    public void handleCommaPressed(ActionEvent actionEvent) {
        calculatorEngine.addComma();
    }

    @FXML
    public void handleArithmeticOperation(ActionEvent actionEvent) {
        String operationSign = ((Button) actionEvent.getSource()).getText();
        calculatorEngine.doOperation(operationSign);
    }

    @FXML
    public void handleShowResult(ActionEvent actionEvent) {
        calculatorEngine.calculateResult();
    }

}