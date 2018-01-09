package Controllers;

import Models.ChordModel;
import Models.MainModel;
import Objects.Accord;
import Objects.Player;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Chords extends Controller implements Initializable {

    // ---- Chord Grid ---- //
    public GridPane chordGrid;

    // ---- Accords ---- //
    public Pane c_chord;
    public Pane d_chord;
    public Pane e_chord;
    public Pane f_chord;
    public Pane g_chord;
    public Pane a_chord;
    public Pane b_chord;

    // ---- Création accord ---- //
    public RadioButton radioMajeur;
    public RadioButton radioMineur;
    public TextField textFieldGamme;
    public Button lessGamme;
    public Button addGamme;
    public CheckBox checkBox_playNoteHover;
    public TextField masterTempo;


    // ---- Play Button ---- //
    public Button playBtn;
    public Button resetBtn;

    // ---- Accord - Détail ---- //
    public Pane cd_pane;
    public Button cd_delete;
    public Label cd_name;
    public RadioButton cd_radioMajeur;
    public RadioButton cd_radioMineur;
    public TextField cd_gamme;
    public Button cd_lessGamme;
    public Button cd_addGamme;

    private final ToggleGroup createChordGroup = new ToggleGroup();
    private final ToggleGroup detailsChordGroup = new ToggleGroup();

    private Pane[] chordsPane  = new Pane[]{c_chord, d_chord, e_chord, f_chord, g_chord, a_chord, b_chord};
    private ArrayList<Label> labelsChordGrid = new ArrayList<>();

    private ArrayList<Integer> chordList = new ArrayList<>();

    private HashMap<Pane, Integer> paneToChord;

    private ChordModel chordModel;
    private MainModel model;

    private boolean isPlaying = false;

    private Boolean majeur;
    private Boolean playNoteHover;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
            /!\ ATTENTION /!\
            Ici le controller n'est pas encore chargé.
            La méthode initialize est appelée lorsque l'on fait FXMLLoader.load(); (CF application.Controller - @loadView() )
         */
        radioMajeur.fire();
        cd_pane.setVisible(false);
        checkBox_playNoteHover.fire();

        // ---- Toggle group for radio ----
        // Create Chord
        radioMajeur.setToggleGroup(createChordGroup);
        radioMineur.setToggleGroup(createChordGroup);
        createChordGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> majeur = newValue == radioMajeur);

        // Chord Details
        cd_radioMajeur.setToggleGroup(detailsChordGroup);
        cd_radioMineur.setToggleGroup(detailsChordGroup);

        // Gamme add / less
        addGamme.setOnMouseClicked(event -> textFieldGamme.setText(String.valueOf(getGamme() + ( (getGamme() < 9) ? 1 : 0 ) )));
        lessGamme.setOnMouseClicked(event -> textFieldGamme.setText(String.valueOf(getGamme() - ( (getGamme() > 0) ? 1 : 0 ))));
        cd_addGamme.setOnMouseClicked(event -> cd_gamme.setText(String.valueOf(getCDGamme() + ( (getCDGamme() < 9) ? 1 : 0 ) )));
        cd_lessGamme.setOnMouseClicked(event -> cd_gamme.setText(String.valueOf(getCDGamme() - ( (getCDGamme() > 0) ? 1 : 0 ))));

        // Play Btn
        playBtn.setOnMouseClicked(event -> {
            if ( ! isPlaying )
            {
                playBtn.setText("Pause");
                try {
                    model.player.createTrackFromNotes(chordList);
                    model.player.sequencer.start();
                } catch (InvalidMidiDataException e) {
                    e.printStackTrace();
                }
                isPlaying = true;
            }
            else
            {
                playBtn.setText("Play");
                model.player.sequencer.stop();
                isPlaying = false;
            }
        });

        // Reset Btn
        resetBtn.setOnMouseClicked(event -> {
            if ( model.player.sequencer.isRunning() )
            {
                model.player.sequencer.stop();
                isPlaying = false;
                playBtn.setText("Play");
            }
            cd_pane.setVisible(false);
            chordGrid.getChildren().clear();
            labelsChordGrid.clear();
            chordList.clear();
        });

        // Tempo TextField
        masterTempo.textProperty().addListener((observable, oldValue, newValue) -> {
            if ( newValue.isEmpty() || Objects.equals(newValue, "")) return;
            if ( !newValue.matches("\\d") )
            {
                String value = newValue.replaceAll("[^\\d]", "");
                masterTempo.setText(value);
                model.player.sequencer.setTempoInBPM(Float.parseFloat(value));
            }
        });
    }

    private void init() {

        try {
            model.player = new Player();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

        paneToChord = new HashMap<Pane, Integer>();
        paneToChord.put(c_chord,  0);
        paneToChord.put(d_chord,  2);
        paneToChord.put(e_chord,  4);
        paneToChord.put(f_chord,  5);
        paneToChord.put(g_chord,  7);
        paneToChord.put(a_chord,  9);
        paneToChord.put(b_chord,  11);

        paneToChord.forEach((key, value) -> key.setOnMouseEntered(event -> {
                if ( checkBox_playNoteHover.isSelected() )
                    model.player.playNote(value + ( 12 * getGamme() ));
        }));

        paneToChord.forEach((key, value) -> key.setOnMouseClicked(event -> {
            model.player.playNote(value + ( 12 * getGamme() ));
        }));




        // ---- Drag'N'Drop Chords Feature ---- //
        paneToChord.forEach((pane, value) -> pane.setOnDragDetected(event -> {
            Dragboard db = pane.startDragAndDrop(TransferMode.ANY);

            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(value));
            db.setContent(content);

            event.consume();
        }));

        chordGrid.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);

            event.consume();
        });

        chordGrid.setOnDragEntered(event -> {
           chordGrid.setStyle("-fx-background-color: green");

           event.consume();
        });

        chordGrid.setOnDragExited(event -> {
            chordGrid.setStyle("-fx-background-color: #eee");

            event.consume();
        });

        chordGrid.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if ( db.hasString() )
            {
                Label l = getChordGridLabel(String.valueOf(Integer.parseInt(db.getString()) + 12 * getGamme()));
                chordGrid.add(l, chordList.size(), 0);
                chordList.add(Integer.parseInt(db.getString()) + ( 12 * getGamme() ));

                ColumnConstraints columnConstraints = new ColumnConstraints();
                columnConstraints.setPrefWidth(40);
                chordGrid.getColumnConstraints().add(columnConstraints);

                labelsChordGrid.add(l);

                updtLabelsEvent();
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void updtLabelsEvent() {
        int i = 0;
        for ( i = 0; i < labelsChordGrid.size(); i++ ) {
            int finalI = i;
            labelsChordGrid.get(i).setOnMouseClicked(event -> updateCDPane(finalI));
        }
    }

    private void updateCDPane(int index) {
        int c_note = chordList.get(index)%12;

        cd_pane.setVisible(true);
        cd_name.setText("Note " + chordList.get(index) + "( "+ c_note +" )");
        cd_gamme.setText(String.valueOf(chordList.get(index) / 12));

        cd_addGamme.setOnMouseClicked(event->{
            int newNote = c_note + ( (Integer.parseInt(cd_gamme.getText()) + 1) * 12 );
            chordList.set(index, newNote);
            cd_name.setText("Note " + newNote + "( "+ c_note +" )");
            labelsChordGrid.get(index).setText(String.valueOf(newNote));
            cd_gamme.setText(String.valueOf(Integer.parseInt(cd_gamme.getText()) + 1));
        });

        cd_lessGamme.setOnMouseClicked(event->{
            int newNote = c_note + ( (Integer.parseInt(cd_gamme.getText()) - 1) * 12 );
            chordList.set(index, newNote);
            cd_name.setText("Note " + newNote + "( "+ c_note +" )");
            labelsChordGrid.get(index).setText(String.valueOf(newNote));
            cd_gamme.setText(String.valueOf(Integer.parseInt(cd_gamme.getText()) - 1));
        });
    }

    private Label getChordGridLabel(String val)
    {
        Label l = new Label(val);
        l.setStyle("-fx-background-color: #bbb; -fx-border-color: black");
        l.setPrefHeight(165);
        l.setPrefWidth(100);
        l.setAlignment(Pos.CENTER);
        l.setTextAlignment(TextAlignment.CENTER);

        return l;
    }

    private int getGamme()
    {
        String gammeValue = textFieldGamme.getText();
        return Integer.parseInt(gammeValue);
    }

    private int getCDGamme()
    {
        String gammeValue = cd_gamme.getText();
        return Integer.parseInt(gammeValue);
    }

    public void setModel(MainModel model) {
        this.model = model;
        this.chordModel = model.chordModel;
        init();
    }


}