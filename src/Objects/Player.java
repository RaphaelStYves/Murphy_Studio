package Objects;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import javax.sound.midi.*;
import java.util.ArrayList;

public class Player {

    private Sequencer sequencer;

    private MidiChannel midiChannel;

    private Receiver receiver;
    private Transmitter transmitter;
    private Synthesizer synthesizer;

    private MidiDevice ouputDevice;

    public SimpleIntegerProperty master_volume;

    private int tick = 4;

    private Sequence sequence;
    private Track track;

    public Player() throws MidiUnavailableException {
        master_volume = new SimpleIntegerProperty(50);
        initSequencer();
    }

    ShortMessage createMidiMessage(int command, int channel, int data1, int data2) throws InvalidMidiDataException {
        ShortMessage sm = new ShortMessage();
        sm.setMessage(command, channel, data1, data2);
        return sm;
    }

    public void playChord(Accord chord, boolean start) throws InvalidMidiDataException {
        for ( int i = 0; i < chord.getNotes().length; i++ )
        {
            ShortMessage sm = createMidiMessage(
                    (start) ? ShortMessage.NOTE_ON : ShortMessage.NOTE_OFF,
                    0,
                    chord.getNotes()[i],
                    93
            );
            long timeStamp = -1;

            receiver.send(sm, timeStamp);
        }
    }

    private void initSequencer() throws MidiUnavailableException
    {
        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();

        sequencer = MidiSystem.getSequencer();
        sequencer.open();

        try {
            sequence = new Sequence(Sequence.PPQ, tick);
            track = sequence.createTrack();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }

        this.midiChannel = synthesizer.getChannels()[0];

        receiver = new CustomReceiver(midiChannel);
    }

    private void addNoteToTrack(int note, int start,int velocity, int duration)
    {
        track.add(createMidiEvent(ShortMessage.NOTE_ON, 1, note, velocity, start));
        track.add(createMidiEvent(ShortMessage.NOTE_OFF, 1, note, velocity, start+duration));
    }

    public void createTrackFromChords(Accord[] partition) throws InvalidMidiDataException {
        for ( int i = 0; i < partition.length; i++ )
            for (int note : partition[i].getNotes() ) addNoteToTrack(note, i * 8, 93, 8);

        sequencer.setSequence(sequence);
        sequencer.setLoopCount(160);
        sequencer.setTempoInBPM(320);
        sequencer.start();
    }

    private MidiEvent createMidiEvent(int command, int channel, int data1, int data2, int tick)
    {
        MidiEvent midiEvent = null;
        try{
            ShortMessage msg = createMidiMessage(command, channel, data1, data2);
            midiEvent = new MidiEvent(msg, tick);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        return midiEvent;
    }
}
