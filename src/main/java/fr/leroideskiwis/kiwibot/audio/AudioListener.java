package fr.leroideskiwis.kiwibot.audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class AudioListener implements AudioReceiveHandler {

    private AudioSender sender;

    public AudioListener(AudioSender s){
        this.sender = s;

    }

    public boolean canReceiveCombined() {
        return true;
    }


    public boolean canReceiveUser() {
        return false;
    }


    @Override
    public void handleCombinedAudio(CombinedAudio arg0) {

        sender.sendBytes(arg0.getAudioData(1.0));

    }

    @Override
    public void handleUserAudio(UserAudio userAudio) {

    }

}