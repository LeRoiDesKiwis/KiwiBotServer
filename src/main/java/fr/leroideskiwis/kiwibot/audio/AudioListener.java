package fr.leroideskiwis.kiwibot.audio;

import javazoom.jl.decoder.Header;
import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;

import java.io.*;

public class AudioListener implements AudioReceiveHandler {

    public boolean canReceiveCombined() {
        return true;
    }

    private FileOutputStream stream;

    public boolean canReceiveUser() {
        return false;
    }

    public AudioListener(File file) throws IOException {

        this.stream = new FileOutputStream(file);
        //HEADER
        this.stream.write(new byte[]{73, 68, 51});

    }

    public void close() throws IOException {
        this.stream.flush();
        this.stream.close();
    }

    @Override
    public void handleCombinedAudio(CombinedAudio arg0) {
        try {
            this.stream.write(arg0.getAudioData(1.0));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void handleUserAudio(UserAudio userAudio) {

    }

}