package eu.mapidev.calculator.phoenix;

import eu.mapidev.calculator.phoenix.engine.CalculatorEngine;
import eu.mapidev.calculator.phoenix.engine.CalculatorInput;
import eu.mapidev.calculator.phoenix.engine.CalculatorState;
import eu.mapidev.calculator.phoenix.engine.Command;
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
    private CalculatorState state = CalculatorState.initial();
    private double dragOffsetX;
    private double dragOffsetY;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.setOnMousePressed(pressEvent -> {
            dragOffsetX = pressEvent.getSceneX();
            dragOffsetY = pressEvent.getSceneY();
        });
        root.setOnMouseDragged(dragEvent -> {
            if (primaryStage == null)
                return;

            primaryStage.setX(dragEvent.getScreenX() - dragOffsetX);
            primaryStage.setY(dragEvent.getScreenY() - dragOffsetY);
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

        showState();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void handleMinimize(ActionEvent actionEvent) {
        primaryStage.setIconified(true);
    }

    @FXML
    public void handleClose(ActionEvent actionEvent) {
        primaryStage.close();
    }

    @FXML
    public void handleDigitPressed(ActionEvent actionEvent) {
        accept(CalculatorInput.fromToken(((Button) actionEvent.getSource()).getText()));
    }

    @FXML
    public void handlePercentPressed(ActionEvent actionEvent) {
        accept(Command.PERCENT);
    }

    @FXML
    public void handleReset(ActionEvent actionEvent) {
        accept(Command.CLEAR);
    }

    @FXML
    public void handleChangeSign(ActionEvent actionEvent) {
        accept(Command.CHANGE_SIGN);
    }

    @FXML
    public void handleCommaPressed(ActionEvent actionEvent) {
        accept(Command.COMMA);
    }

    @FXML
    public void handleArithmeticOperation(ActionEvent actionEvent) {
        accept(CalculatorInput.fromToken(((Button) actionEvent.getSource()).getText()));
    }

    @FXML
    public void handleShowResult(ActionEvent actionEvent) {
        accept(Command.EQUALS);
    }

    private void accept(CalculatorInput input) {
        state = CalculatorEngine.apply(state, input);
        showState();
    }

    private void showState() {
        lblResult.setText(state.display());
        lblMemory.setText(state.memory());
    }
}