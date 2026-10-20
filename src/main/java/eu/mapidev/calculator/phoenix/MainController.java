package eu.mapidev.calculator.phoenix;

import eu.mapidev.calculator.phoenix.engine.CalculatorInput;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainController implements Initializable {

    @FXML
    private GridPane root;
    @FXML
    private Label lblResult;
    @FXML
    private Label lblMemory;

    private final CalculatorViewModel viewModel = new CalculatorViewModel();
    private Stage primaryStage;
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

        // Fails fast at startup if a calculator button has a missing, unknown or duplicate userData token
        Map<CalculatorInput, Button> buttonsByInput = root.getChildren().stream()
                .flatMap(node -> node instanceof Button button ? Stream.of(button) : Stream.empty())
                .collect(Collectors.toMap(MainController::inputOf, Function.identity()));

        root.setOnKeyPressed(keyEvent -> CalculatorKeyBindings.findInputByEvent(keyEvent)
                .map(buttonsByInput::get)
                .ifPresent(CalculatorKeyBindings::fireWithClickAnimation));

        lblResult.textProperty().bind(viewModel.resultProperty());
        lblMemory.textProperty().bind(viewModel.memoryProperty());
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
    public void handleInput(ActionEvent actionEvent) {
        viewModel.accept(inputOf((Button) actionEvent.getSource()));
    }

    // Buttons carry their input token in userData, so the label text can change freely
    private static CalculatorInput inputOf(Button button) {
        if (!(button.getUserData() instanceof String token))
            throw new IllegalStateException("Calculator button '" + button.getText() + "' has no userData token");

        return CalculatorInput.fromToken(token);
    }
}
