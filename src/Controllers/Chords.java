package Controllers;

import Models.ChordModel;
import Models.MainModel;
import Objects.Accord;
import Objects.Player;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.net.URL;
import java.util.*;

public class Chords extends Controller implements Initializable {

    private ChordModel chordModel;
    private MainModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
            /!\ ATTENTION /!\
            Ici le controller n'est pas encore chargé.
            La méthode initialize est appelée lorsque l'on fait FXMLLoader.load(); (CF application.Controller - @loadView() )
         */
    }

    private void init() {

        try {
            model.player = new Player();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

    }

    private void playChord(Accord chord)
    {
        Accord newChord = chord.getWithScale(1);
        System.out.println(newChord.getName() + " (" + newChord.getShortName() + ") : " + Arrays.toString(newChord.getNotes()));
        try {
            this.model.player.playChord(newChord.getWithScale(4), true);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    public void setModel(MainModel model) {
        this.model = model;
        this.chordModel = model.chordModel;
        init();
    }


}