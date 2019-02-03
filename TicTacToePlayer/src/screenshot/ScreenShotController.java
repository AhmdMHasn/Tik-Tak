package screenshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class ScreenShotController implements Initializable {

    @FXML
    private ImageView imageView;

    @FXML
    private Button share;
    WritableImage image;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setImage(WritableImage image) {
        imageView.setImage(image);
        this.image = image;
    }

    @FXML
    private void share(ActionEvent event) throws IOException {
        Stage satge = new Stage();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Save Image");
        System.out.println(image);
        File file = fileChooser.showSaveDialog(satge);
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image,
                        null), "png", file);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }
}
