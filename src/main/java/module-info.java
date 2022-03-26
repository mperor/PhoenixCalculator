module phoenix {
    requires javafx.controls;
    requires javafx.fxml;


    opens eu.mapidev.calculator.phoenix to javafx.fxml;
    exports eu.mapidev.calculator.phoenix;
}