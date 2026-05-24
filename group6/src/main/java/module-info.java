module group6.java.group6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;


    opens group6.java.group6 to javafx.fxml;
    exports group6.java.group6;
}