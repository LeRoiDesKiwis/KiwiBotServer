package fr.leroideskiwis.kiwibot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;

public class MusicPlayer {
    private final AudioPlayer player;
    private final AudioListener listener;
    private final Guild guild;

    public MusicPlayer(AudioPlayer player, Guild guild) {
        this.player = player;
        this.guild = guild;
        listener = new AudioListener(this);
        player.addListener(listener);

    }

    public AudioPlayer getAudioPlayer() {
        return player;
    }

    public AudioListener getListener() {
        return listener;
    }

    public Guild getGuild() {
        return guild;
    }

    public AudioHandler getAudioHandler(){
        return new AudioHandler(player);
    }

    public synchronized void playTrack(AudioTrack track){
        listener.queue(track);
    }

    public synchronized void skipTrack(){
        listener.nextTrack();
    }

}
