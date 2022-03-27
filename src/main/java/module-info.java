module phoenix {
    requires javafx.controls;
    requires javafx.fxml;

    opens img;

    opens eu.mapidev.calculator.phoenix to javafx.fxml;
    exports eu.mapidev.calculator.phoenix;
}