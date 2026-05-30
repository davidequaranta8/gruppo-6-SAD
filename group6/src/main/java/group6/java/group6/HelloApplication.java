package group6.java.group6;

import atlantafx.base.theme.CupertinoDark;
import group6.java.group6.db.DatabaseResource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("myMainView.fxml"));
        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
        Scene scene = new Scene(fxmlLoader.load(), 1300, 800);
        stage.setTitle("Music Playlist Manager");
        stage.setScene(scene);
        stage.show();
        DatabaseResource.getInstance(); // per creare le table del DB
        //Comment
    }
}
