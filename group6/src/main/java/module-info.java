module group6.java.group6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires java.sql;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires jdk.compiler;
    requires java.desktop;
    requires javafx.media;
    requires mp3agic;
    opens group6.java.group6 to javafx.fxml;
    exports group6.java.group6;
    exports group6.java.group6.controllers;
    opens group6.java.group6.controllers to javafx.fxml;
    opens group6.java.group6.models to javafx.base;
    opens group6.java.group6.utils to javafx.base;
    exports group6.java.group6.helper;
    opens group6.java.group6.helper to javafx.fxml;
    exports group6.java.group6.states;
    opens group6.java.group6.states to javafx.fxml;
}