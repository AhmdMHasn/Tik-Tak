package Interface;

import java.io.Serializable;
import javafx.scene.image.ImageView;

/**
 *
 * @author elmahask
 */
public class Player implements Serializable {

    private String userName;
    private String password;
    private int status;
    private String gender;
    private int win;
    private int lose;
    private int draw;
    private int totalMatches;
    private ImageView myImage;

    public ImageView getMyImage() {
        return myImage;
    }

    public void setMyImage(ImageView myImage) {
        this.myImage = myImage;
    }

    public Player(ImageView myImage, String userName, String gender, int totalMatches) {
        this.myImage = myImage;
        this.userName = userName;
        this.gender = gender;
        this.totalMatches = totalMatches;
    }
      public Player(int status, ImageView myImage, String userName, String gender, int totalMatches) {
        this.status = status;
        this.myImage = myImage;
        this.userName = userName;
        this.gender = gender;
        this.totalMatches = totalMatches;
    }
    

    public Player(int status, String userName, String gender, int win) {
        this.userName = userName;
        this.status = status;
        this.gender = gender;
        this.win = win;
    }

    public Player() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
    }
}
