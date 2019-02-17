package fr.leroideskiwis.kiwibot.audio;

import javazoom.jl.decoder.Header;
import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;

import java.io.*;
import java.util.Scanner;

public class AudioListener implements AudioReceiveHandler {

    private final File file;

    public boolean canReceiveCombined() {
        return true;
    }

    private FileOutputStream stream;

    public boolean canReceiveUser() {
        return false;
    }

    public AudioListener(String path) throws IOException {
        this.file = new File(path+".pcm");
        this.stream = new FileOutputStream(file);

    }

    public void close() throws IOException {
        this.stream.flush();
        this.stream.close();
        File file2 = new File(file.getName().replace(".pcm", "")+".wav");
        new PcmToWav().rawToWave(file, file2);

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