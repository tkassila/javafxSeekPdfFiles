package com.metait.javafxseekpdffiles;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.io.IOException;

public class SeekPdfFilesApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            SeekPdfFilesController controller = new SeekPdfFilesController();
            FXMLLoader fxmlLoader = new FXMLLoader(SeekPdfFilesApplication.class.getResource("seekpdffiles-view.fxml"));
            fxmlLoader.setController(controller);
            Scene scene = new Scene(fxmlLoader.load(), 1120, 740);
            stage.setTitle("Seek pdf files");
            controller.setStage(stage);
            stage.setScene(scene);
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}