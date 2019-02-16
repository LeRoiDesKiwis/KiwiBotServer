package fr.leroideskiwis.kiwibot.commands;

import fr.leroideskiwis.kiwibot.command.Command;
import fr.leroideskiwis.kiwibot.music.MusicManager;
import fr.leroideskiwis.kiwibot.music.MusicPlayer;
import fr.leroideskiwis.kiwibot.utils.Utils;
import javafx.scene.text.TextBoundsType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;

import javax.xml.soap.Text;

public class CommandMusic {

    private final MusicManager manager = new MusicManager();

    @Command(name="play")
    public void play(Utils ut, Guild g, TextChannel c, User u, String[] args){
        if(g == null) return;

        if(!g.getAudioManager().isConnected() && !g.getAudioManager().isAttemptingToConnect()){
            VoiceChannel channel = g.getMember(u).getVoiceState().getChannel();
            if(channel == null){
                c.sendMessage(ut.getErrorEmbed("Vous devez être connecté à un salon vocal !")).queue();
                return;
            }
            g.getAudioManager().openAudioConnection(channel);
        }

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < args.length; i++){
            builder.append(args[i]);

            if(i != args.length-1) builder.append(" ");
        }

        manager.loadTrack(c, builder.toString());
    }

    @Command(name="skip")
    public void skip(Guild g, TextChannel c){
        if((!g.getAudioManager().isAttemptingToConnect() && !g.getAudioManager().isConnected())){
            c.sendMessage("Le bot n'a pas de piste en cours.").queue();
            return;
        }
        manager.getPlayer(g).skipTrack();
        c.sendMessage("Le bot est passé à la piste suivante.").queue();
    }

    @Command(name="clear")
    public void clear(Guild g, TextChannel c){

        MusicPlayer player = manager.getPlayer(g);

        if(player.getListener().getTracks().isEmpty()){
            c.sendMessage("Aucune piste dans la file d'attente.").queue();
        }

        player.getListener().getTracks().clear();
        c.sendMessage("La liste d'attente à été vidée.").queue();

    }
}
