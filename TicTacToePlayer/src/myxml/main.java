/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myxml;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author A7med
 */
public class main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        JavaToXML toXMLClass = new JavaToXML();
        toXMLClass.addItemsToList(1, "X", 0, 0);
        toXMLClass.addItemsToList(2, "O", 1, 1);
        toXMLClass.addItemsToList(3, "X", 2, 1);
        toXMLClass.addItemsToList(4, "o", 0, 1);
        toXMLClass.addItemsToList(5, "x", 2, 0);

        toXMLClass.writeToFIle("Ahmed");

        Parent root = FXMLLoader.load(getClass().getResource("Record.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
