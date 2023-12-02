package eu.mapidev.calculator.phoenix;

import eu.mapidev.calculator.phoenix.logic.CalculatorEngine;
import eu.mapidev.calculator.phoenix.utils.ArithmeticOperation;
import eu.mapidev.calculator.phoenix.utils.CalculatorDigit;
import eu.mapidev.calculator.phoenix.utils.CalculatorKeyBindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        var buttonsByTextName = root.getChildren().stream()
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node)
                .collect(Collectors.toMap(Labeled::getText, Function.identity()));

        root.setOnKeyPressed(keyEvent -> {
            var button = buttonsByTextName.get(CalculatorKeyBindings.findButtonTextByEvent(keyEvent));
            if (button != null)
                CalculatorKeyBindings.buttonFireWithClickAnimation(button);
        });

        calculatorEngine = new CalculatorEngine();
        calculatorEngine.setOnResultChanged(lblResult::setText);
        calculatorEngine.setOnMemoryChanged(lblMemory::setText);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void handleDigitPressed(ActionEvent actionEvent) {
        String buttonTextDigit = ((Button) actionEvent.getSource()).getText();
        CalculatorDigit digit = CalculatorDigit.fromString(buttonTextDigit)
                        .orElseThrow(IllegalArgumentException::new);
        calculatorEngine.inputDigit(digit);
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
        ArithmeticOperation operation = ArithmeticOperation.findBySign(operationSign)
                .orElseThrow(IllegalArgumentException::new);

        tryCatchArithmeticException(() -> calculatorEngine.doOperation(operation));
    }

    @FXML
    public void handleShowResult(ActionEvent actionEvent) {
        tryCatchArithmeticException(() -> calculatorEngine.calculateResult());
    }

    private void tryCatchArithmeticException(Runnable runnable) {
        try {
            runnable.run();
        } catch (ArithmeticException e) {
            lblResult.setText("undefined");
        }
    }
}