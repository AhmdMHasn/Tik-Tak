/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 */
public class GameplayController implements Initializable {

    @FXML
    private VBox vBox;

    @FXML
    private Label lblStatus;

    @FXML
    private GridPane grid_pane;

    @FXML
    private Label player_score;

    @FXML
    private Label player_name;

    @FXML
    private Label computer_score;

    @FXML
    private Label computer_name;

    @FXML
    private Label draw_score;

    @FXML
    private Label draw_name;

    @FXML
    private Button play_again;

    private char player = 'X';
    private char comp = 'O';
    public static int counter = 1;
    public static String firstMove;
    public static boolean end = false;
    public static int pointsOfComputer;
    public static int pointsOfPlayer;
    public static int pointsOfDraw;
    public boolean playerTurn = true;

    Line line1, line2;
    Ellipse ellipse;

    // Create and initialize cell
    private Cell[][] cell = new Cell[3][3];

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        drawGame();
        resetGame();
        pointsOfComputer = 0;
        pointsOfDraw = 0;
        pointsOfPlayer = 0;

        play_again.setOnAction(__ -> {

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    cell[i][j].getChildren().clear();
                    cell[i][j].setToken(' ');

                }
            }

            resetGame();

        });

    }

    // To draw cell on grid pane
    public void drawGame() {

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid_pane.add(cell[i][j] = new Cell(), j, i);
            }
        }
    }

    // To reset all variable like the first of game
    public void resetGame() {
        lblStatus.setText("X's turn to play");
        end = false;
        counter = 1;
        firstMove = " ";
        playerTurn = true;
    }

    // Determine if the play is draw
    public boolean isFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cell[i][j].getToken() == ' ') {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Determine if the player with the specified token wins
     */
    public boolean isWon(char token) {
        for (int i = 0; i < 3; i++) {
            if (cell[i][0].getToken() == token
                    && cell[i][1].getToken() == token
                    && cell[i][2].getToken() == token) {
                return true;
            }
        }

        for (int j = 0; j < 3; j++) {
            if (cell[0][j].getToken() == token
                    && cell[1][j].getToken() == token
                    && cell[2][j].getToken() == token) {
                return true;
            }
        }

        if (cell[0][0].getToken() == token
                && cell[1][1].getToken() == token
                && cell[2][2].getToken() == token) {
            return true;
        }

        if (cell[0][2].getToken() == token
                && cell[1][1].getToken() == token
                && cell[2][0].getToken() == token) {
            return true;
        }

        return false;
    }

    // An inner class for a cell
    public class Cell extends Pane {
        // Token used for this cell

        private char token = ' ';

        public Cell() {
            setStyle("-fx-border-color: #2E2E2D");
            this.setPrefSize(2000, 2000);
            this.setOnMouseClicked(e -> {
                playerTurn();
            });
        }

        /**
         * Return token
         */
        public char getToken() {
            return token;
        }

        /**
         * Set a new token
         */
        public void setToken(char c) {
            token = c;

            if (token == 'X') {
                line1 = new Line(15, 15,
                        this.getWidth() - 15, this.getHeight() - 15);
                line1.endXProperty().bind(this.widthProperty().subtract(15));
                line1.endYProperty().bind(this.heightProperty().subtract(15));
                line1.setStroke(Color.ORANGE);
                line1.setStrokeLineJoin(StrokeLineJoin.ROUND);
                line1.setStrokeWidth(5);
                line2 = new Line(15, this.getHeight() - 15,
                        this.getWidth() - 15, 15);
                line2.startYProperty().bind(
                        this.heightProperty().subtract(15));
                line2.endXProperty().bind(this.widthProperty().subtract(15));
                line2.setStroke(Color.ORANGE);
                line2.setStrokeWidth(5);
                line2.setStrokeLineJoin(StrokeLineJoin.ROUND);

                // Add the lines to the pane
                this.getChildren().addAll(line1, line2);

                FadeTransition ft1 = new FadeTransition(Duration.millis(800), line1);
                ft1.setFromValue(.3);
                ft1.setToValue(1);
                ft1.setCycleCount(1);
                ft1.setAutoReverse(false);

                FadeTransition ft2 = new FadeTransition(Duration.millis(800), line2);
                ft2.setFromValue(.3);
                ft2.setToValue(1);
                ft2.setCycleCount(1);
                ft2.setAutoReverse(false);

                ft1.play();
                ft2.play();

            } else if (token == 'O') {
                ellipse = new Ellipse(this.getWidth() / 2,
                        this.getHeight() / 2, this.getWidth() / 2 - 15,
                        this.getHeight() / 2 - 15);
                ellipse.centerXProperty().bind(
                        this.widthProperty().divide(2));
                ellipse.centerYProperty().bind(
                        this.heightProperty().divide(2));
                ellipse.radiusXProperty().bind(
                        this.widthProperty().divide(2).subtract(15));
                ellipse.radiusYProperty().bind(
                        this.heightProperty().divide(2).subtract(15));
                ellipse.setStroke(Color.GREEN);
                ellipse.setFill(Color.web("f4f4f4"));
                ellipse.setStrokeWidth(5);

                getChildren().add(ellipse); // Add the ellipse to the pane

                FadeTransition ft = new FadeTransition(Duration.millis(1500), ellipse);
                ft.setFromValue(.4);
                ft.setToValue(1);
                ft.setCycleCount(1);
                ft.setAutoReverse(false);
                ft.play();
            }

        }

        private boolean compTurn() {

            //<editor-fold defaultstate="collapsed" desc="first move of the computer">
            if (counter == 1) {
                //if the human moved in the center
                if (cell[1][1].getToken() == player) {
                    Random rand = new Random();
                    int f = rand.nextInt(4) + 1;
                    if (f == 1) {
                        //then it is 00
                        cell[0][0].setToken(comp);
                        firstMove = "00";

                        //System.out.println("CHECK");
                    } else if (f == 2) {
                        //then it is 00
                        cell[0][2].setToken(comp);
                        firstMove = "02";

                        //System.out.println("CHECK");
                    } else if (f == 3) {
                        //then it is 00

                        cell[2][0].setToken(comp);
                        firstMove = "20";

                        //System.out.println("CHECK");
                    } else if (f == 4) {
                        //then it is 00
                        cell[2][2].setToken(comp);
                        firstMove = "22";

                        //System.out.println("CHECK");
                    } else {
                        //then it is 00
                        cell[2][2].setToken(comp);
                        firstMove = "22";

                        //System.out.println("CHECK");
                    }
                } else {
                    cell[1][1].setToken(comp);
                    firstMove = "11";

                }

            }

            //<editor-fold defaultstate="collapsed" desc="second move of the computer">
            if (counter == 2) {
                int second[] = thirdMove();
                //if there is no defensice move
                if (second[0] == 5) {
                    // o player , x Computer
                    if (cell[1][1].getToken() != comp) {
                        if (firstMove == "00" && cell[0][2].getToken() == ' ') {
                            cell[0][2].setToken(comp);

                        } else if (firstMove == "00" && cell[2][0].getToken() == ' ') {
                            cell[2][0].setToken(comp);

                        } else if (firstMove == "02" && cell[0][0].getToken() == ' ') {
                            cell[0][0].setToken(comp);

                        } else if (firstMove == "02" && cell[2][2].getToken() == ' ') {
                            cell[2][2].setToken(comp);

                        } else if (firstMove == "20" && cell[0][0].getToken() == ' ') {
                            cell[0][0].setToken(comp);

                        } else if (firstMove == "20" && cell[2][2].getToken() == ' ') {
                            cell[2][2].setToken(comp);

                        } else if (firstMove == "22" && cell[2][0].getToken() == ' ') {
                            cell[2][0].setToken(comp);

                        } else if (firstMove == "22" && cell[0][2].getToken() == ' ') {
                            cell[0][2].setToken(comp);

                        }
                    } else {
                        if (cell[0][0].getToken() == ' ') {
                            cell[0][0].setToken(comp);

                        } else if (cell[0][2].getToken() == ' ') {
                            cell[0][2].setToken(comp);

                        } else if (cell[2][0].getToken() == ' ') {
                            cell[2][0].setToken(comp);

                        } else if (cell[2][2].getToken() == ' ') {
                            cell[2][2].setToken(comp);

                        }
                    }

                } //if there is a defensive move
                else {
                    cell[second[0]][second[1]].setToken(comp);

                }

            }

            // o player , x Computer
            //<editor-fold defaultstate="collapsed" desc="third ">
            if (counter == 3) {

                int second[] = thirdMove();
                if (second[0] != 5) {
                    //System.out.println("CHECK");
                    cell[second[0]][second[1]].setToken(comp);

                } else {
                    int secondMy[] = thirdMyMove();
                    if (secondMy[0] != 5) {
                        cell[secondMy[0]][secondMy[1]].setToken(comp);

                    } else {
                        secondMy = noOtherOption();
                        cell[secondMy[0]][secondMy[1]].setToken(comp);

                    }
                }

            }

            //<editor-fold defaultstate="collapsed" desc="fourth">
            if (counter == 4) {

                int second[] = thirdMove();
                if (second[0] != 5) {
                    //System.out.println("CHECK");
                    cell[second[0]][second[1]].setToken(comp);

                } else {
                    int secondMy[] = thirdMyMove();
                    if (secondMy[0] != 5) {
                        cell[secondMy[0]][secondMy[1]].setToken(comp);

                    } else {
                        secondMy = noOtherOption();
                        cell[secondMy[0]][secondMy[1]].setToken(comp);

                    }
                }

            }

            //<editor-fold defaultstate="collapsed" desc="fifth">
            if (counter == 5) {

                int second[] = thirdMove();
                if (second[0] != 5) {
                    //System.out.println("CHECK");
                    cell[second[0]][second[1]].setToken(comp);

                } else {
                    int secondMy[] = thirdMyMove();
                    if (secondMy[0] != 5) {
                        cell[secondMy[0]][secondMy[1]].setToken(comp);

                    } else {
                        secondMy = noOtherOption();
                        cell[secondMy[0]][secondMy[1]].setToken(comp);

                    }
                }

                //}
            }

            counter++;

            // System.out.println(cell[0][0].getChildren());
            return false;

        }

        /* Handle a mouse click event */
        private boolean playerTurn() {

            // If cell is empty and game is not over
            if (!end) {

                if (token == ' ' && player != ' ' && !isWon(comp) && !isWon(player) && playerTurn == true) {
                    setToken(player); // Set token in the cell
                    playerTurn = false;
                }

                if (isWon(player)) {
                    // end = isWon(player);
                    lblStatus.setText(player + " won! The game is over");
                    pointsOfPlayer++;
                    player_score.setText(pointsOfPlayer + "");

                    end = true;
                } else if (isFull()) {
                    lblStatus.setText("Draw! The game is over");
                    pointsOfDraw++;
                    draw_score.setText(pointsOfDraw + "");
                    end = true;
                } else {
                    if (playerTurn == false) {
                        compTurn();
                        playerTurn = true;
                    }

                    if (isWon(comp)) {
                        lblStatus.setText(comp + " won! The game is over");
                        //  comp = ' '; // Game is over
                        pointsOfComputer++;
                        //System.out.println(pointsOfComputer);
                        computer_score.setText(pointsOfComputer + "");
                        end = true;
                    }

                }

            }

            return false;
        }

        // o player , x Computer
        // check the movement of player 
        private int[] thirdMove() {
            //check to see if the human can win in the next move
            //check to see if two of the corners are there first
            int[] ret = new int[2];
            ret[0] = 5;
            if (cell[0][0].getToken() == player && cell[0][1].getToken() == player && cell[0][2].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 2;
            } else if (cell[0][0].getToken() == player && cell[1][0].getToken() == player && cell[2][0].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 0;
            } else if (cell[0][0].getToken() == player && cell[1][1].getToken() == player && cell[2][2].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 2;
            } else if (cell[0][1].getToken() == player && cell[0][2].getToken() == player && cell[0][0].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 0;
            } else if (cell[0][1].getToken() == player && cell[1][1].getToken() == player && cell[2][1].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 1;
            } else if (cell[0][2].getToken() == player && cell[1][1].getToken() == player && cell[2][0].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 0;
            } else if (cell[0][2].getToken() == player && cell[1][2].getToken() == player && cell[2][2].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 2;
            } else if (cell[1][0].getToken() == player && cell[1][1].getToken() == player && cell[1][2].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 2;
            } else if (cell[1][0].getToken() == player && cell[2][0].getToken() == player && cell[0][0].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 0;
            } else if (cell[2][0].getToken() == player && cell[1][1].getToken() == player && cell[0][2].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 2;
            } else if (cell[2][1].getToken() == player && cell[1][1].getToken() == player && cell[0][1].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 1;
            } else if (cell[2][2].getToken() == player && cell[1][1].getToken() == player && cell[0][0].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 0;
            } else if (cell[1][1].getToken() == player && cell[1][2].getToken() == player && cell[1][0].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 0;
            } else if (cell[2][2].getToken() == player && cell[1][2].getToken() == player && cell[0][2].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 2;
            } else if (cell[2][0].getToken() == player && cell[2][1].getToken() == player && cell[2][2].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 2;
            } else if (cell[2][1].getToken() == player && cell[2][2].getToken() == player && cell[2][0].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 0;
            } else if (cell[0][0].getToken() == player && cell[0][2].getToken() == player && cell[0][1].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 1;
            } else if (cell[0][0].getToken() == player && cell[2][0].getToken() == player && cell[1][0].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 0;
            } else if (cell[0][2].getToken() == player && cell[2][2].getToken() == player && cell[1][2].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 2;
            } else if (cell[2][0].getToken() == player && cell[2][2].getToken() == player && cell[2][1].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 1;
            } else if (cell[0][0].getToken() == player && cell[2][2].getToken() == player && cell[1][1].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 1;
            } else if (cell[0][2].getToken() == player && cell[2][0].getToken() == player && cell[1][1].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 1;
            } else if (cell[0][1].getToken() == player && cell[2][1].getToken() == player && cell[1][1].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 1;
            } else if (cell[1][0].getToken() == player && cell[1][2].getToken() == player && cell[1][1].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 1;
            }
            return ret;

        }

        // movement of computer
        private int[] thirdMyMove() {
            //check to see if the human can win in the next move
            //check to see if two of the corners are there first
            int[] ret = new int[2];
            ret[0] = 5;
            if (cell[0][0].getToken() == comp && cell[0][1].getToken() == comp && cell[0][2].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 2;
            } else if (cell[0][0].getToken() == comp && cell[1][0].getToken() == comp && cell[2][0].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 0;
            } else if (cell[0][0].getToken() == comp && cell[1][1].getToken() == comp && cell[2][2].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 2;
            } else if (cell[0][1].getToken() == comp && cell[0][2].getToken() == comp && cell[0][0].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 0;
            } else if (cell[0][1].getToken() == comp && cell[1][1].getToken() == comp && cell[2][1].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 1;
            } else if (cell[0][2].getToken() == comp && cell[1][1].getToken() == comp && cell[2][0].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 0;
            } else if (cell[0][2].getToken() == comp && cell[1][2].getToken() == comp && cell[2][2].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 2;
            } else if (cell[1][0].getToken() == comp && cell[1][1].getToken() == comp && cell[1][2].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 2;
            } else if (cell[1][0].getToken() == comp && cell[2][0].getToken() == comp && cell[0][0].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 0;
            } else if (cell[2][0].getToken() == comp && cell[1][1].getToken() == comp && cell[0][2].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 2;
            } else if (cell[2][1].getToken() == comp && cell[1][1].getToken() == comp && cell[0][1].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 1;
            } else if (cell[2][2].getToken() == comp && cell[1][1].getToken() == comp && cell[0][0].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 0;
            } else if (cell[1][1].getToken() == comp && cell[1][2].getToken() == comp && cell[1][0].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 0;
            } else if (cell[2][2].getToken() == comp && cell[1][2].getToken() == comp && cell[0][2].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 2;
            } else if (cell[2][0].getToken() == comp && cell[2][1].getToken() == comp && cell[2][2].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 2;
            } else if (cell[2][1].getToken() == comp && cell[2][2].getToken() == comp && cell[2][0].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 0;
            } else if (cell[0][0].getToken() == comp && cell[0][2].getToken() == comp && cell[0][1].getToken() == ' ') {
                ret[0] = 0;
                ret[1] = 1;
            } else if (cell[0][0].getToken() == comp && cell[2][0].getToken() == comp && cell[1][0].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 0;
            } else if (cell[0][2].getToken() == comp && cell[2][2].getToken() == comp && cell[1][2].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 2;
            } else if (cell[2][0].getToken() == comp && cell[2][2].getToken() == comp && cell[2][1].getToken() == ' ') {
                ret[0] = 2;
                ret[1] = 1;
            } else if (cell[0][0].getToken() == comp && cell[2][2].getToken() == comp && cell[1][1].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 1;
            } else if (cell[0][2].getToken() == comp && cell[2][0].getToken() == comp && cell[1][1].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 1;
            } else if (cell[0][1].getToken() == comp && cell[2][1].getToken() == comp && cell[1][1].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 1;
            } else if (cell[1][0].getToken() == comp && cell[1][2].getToken() == comp && cell[1][1].getToken() == ' ') {
                ret[0] = 1;
                ret[1] = 1;
            }
            return ret;

        }

        private int[] noOtherOption() {
            int[] ret = new int[2];
            ret[0] = 5;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (cell[i][j].getToken() == ' ') {
                        ret[0] = i;
                        ret[1] = j;
                        break;
                    }
                }
                if (ret[0] != 5) {
                    break;
                }
                //System.out.println();
            }

            return ret;
        }

    }
}
