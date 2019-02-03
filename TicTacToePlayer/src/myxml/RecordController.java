/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myxml;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javax.xml.bind.JAXBException;

/**
 * FXML Controller class
 *
 * @author A7med
 */
public class RecordController implements Initializable {

    @FXML
    private GridPane gridPane = new GridPane();

    @FXML
    private Label playerName;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    void drawOnGridPane() {

        Image x_Image = new Image(getClass().getResourceAsStream("/sources/x.png"));
        Image o_Image = new Image(getClass().getResourceAsStream("/sources/o.png"));

        try {
            Game g = new JavaToXML().readFromFile();
            playerName.setText(g.getId());
            List steps = g.getStep();

            for (int i = 0; i < g.getStep().size(); i++) {
                Step currentStep = (Step) steps.get(i);
                String letter = currentStep.getLetter();
                int move = currentStep.getMove();
                int xPos = currentStep.getXpos();
                int yPos = currentStep.getYpos();
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.millis(1500 + 1500 * i), e -> {

                            System.out.println("Letter: " + letter + ", Move: " + move + ", xPos: " + xPos + ", yPos: " + yPos);

//                            Text t = new Text(letter.toUpperCase());
//                            t.setFont(Font.font("Verdana", 60));
//                            t.setFill(Color.CADETBLUE);
                            Image image = null;
                            if (letter.equalsIgnoreCase("x")) {
                                image = x_Image;
                            } else if (letter.equalsIgnoreCase("o")) {
                                image = o_Image;
                            }

                            ImageView t = new ImageView(image);

                            FadeTransition ft = new FadeTransition(Duration.millis(1200), t);
                            ft.setFromValue(.3);
                            ft.setToValue(1);
                            ft.setCycleCount(1);
                            ft.setAutoReverse(false);

                            gridPane.setHalignment(t, HPos.CENTER); // To align horizontally in the cell
                            gridPane.setValignment(t, VPos.CENTER); // To align vertically in the cell
                            gridPane.add(t, xPos, yPos);
                            ft.play();
                        }));
                timeline.play();
            }

        } catch (JAXBException /*| InterruptedException*/ ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    void drawOnGridPane(ActionEvent event
    ) {
        drawOnGridPane();
    }

}
