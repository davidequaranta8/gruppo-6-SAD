module group6.java.group6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires java.sql;


    opens group6.java.group6 to javafx.fxml;
    exports group6.java.group6;
    exports group6.java.group6.controllers;
    opens group6.java.group6.controllers to javafx.fxml;
}