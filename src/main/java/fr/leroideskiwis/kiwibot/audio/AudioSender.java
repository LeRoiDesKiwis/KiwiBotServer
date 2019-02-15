package fr.leroideskiwis.kiwibot.audio;

import net.dv8tion.jda.core.audio.AudioSendHandler;

public class AudioSender implements AudioSendHandler {

    private byte[] bytes = new byte[3840];

    @Override
    public boolean canProvide() {
        return true;
    }

    public void sendBytes(byte[] bytes){
        if(bytes.length != 3840) return;
        this.bytes = bytes;
    }

    @Override
    public byte[] provide20MsAudio() {

        byte[] bytes1 = this.bytes;
        this.bytes = new byte[3840];

        return bytes1;
    }
}
