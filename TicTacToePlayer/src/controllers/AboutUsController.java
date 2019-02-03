/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author A7med
 */
public class AboutUsController implements Initializable {

    @FXML
    private TextField name00;

    @FXML
    private TextField name01;

    @FXML
    private TextField name02;

    @FXML
    private TextField name03;

    ObservableList<TextField> textFields = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textFields.add(name00);
        textFields.add(name01);
        textFields.add(name02);
        textFields.add(name03);

        initialAnimationDisplay();
    }

    private void initialAnimationDisplay() {
        for (int i = 0; i < textFields.size(); i++) {
            TextField get = (TextField) textFields.get(i);
            get.setVisible(false);
        }
        
        for (int i = 0; i < textFields.size(); i++) {
            TextField get = (TextField) textFields.get(i);
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(1000 + 500 * i), e -> {
                        FadeTransition ft = new FadeTransition(Duration.millis(500), get);
                        get.setVisible(true);
                        ft.setFromValue(.3);
                        ft.setToValue(1);
                        ft.setCycleCount(1);
                        ft.setAutoReverse(false);
                        ft.play();
                    })
            );
            timeline.play();
        }
            
    }
}
