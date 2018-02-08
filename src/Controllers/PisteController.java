package Controllers;

import Models.MainModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import javax.sound.midi.Track;
import java.net.URL;
import java.util.ResourceBundle;

public class PisteController extends Controller
{

    @FXML
    public TextField piste_name_input;
    @FXML
    public Slider piste_volume_slider;
    @FXML
    public ImageView piste_instrument_icon;
    @FXML
    public MenuButton piste_instrument_selection;
    @FXML
    public HBox piste_timeline;
    @FXML
    public ScrollPane scrollpane;
    @FXML
    public Button deletePistePtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
            /!\ ATTENTION /!\
            Ici le controller n'est pas encore chargé.
            La méthode initialize est appelée lorsque l'on fait FXMLLoader.load(); (CF application.Controller - @loadView() )
         */
        /* Tout ce qui agit sur le fxml, tu le code ici */
    }

    private void initAll()
    {
        /* TOut ce qui agit sur le model, tu le code ici */
        this.deletePistePtn.setOnMouseClicked(event -> {
            //TODO Supprimer la piste courante
        });

    }

    public void setName(String name)
    {
        piste_name_input.setText(name);
    }

    public void setModel(MainModel model) {
        this.model = model;
        initAll();
    }

    public void setTrack(Track track) {

    }
}
