package fr.leroideskiwis.kiwibot.audio;

import net.dv8tion.jda.core.audio.AudioSendHandler;

public class SilentSender implements AudioSendHandler {


    @Override
    public boolean canProvide() {
        return false;
    }

    @Override
    public byte[] provide20MsAudio() {
        return new byte[0];
    }
}
