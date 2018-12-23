package fr.leroideskiwis.kiwibot.events;

import fr.leroideskiwis.kiwibot.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;

public class OtherEvents extends ListenerAdapter {

    private Main main;

    public OtherEvents(Main main) {

        this.main = main;

    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        EmbedBuilder builder = new EmbedBuilder().setColor(Color.GREEN).setTitle("Bienvenue "+event.getMember().getUser().getName()+" !");
        builder.setDescription("Passe du bon temps sur notre serveur ! \uD83D\uDE01");

        main.getJda().getTextChannelById(main.getObs().welcomeTX).sendMessage(builder.build()).queue();

        new Thread(() -> {

            try {

                PrivateChannel channel = event.getMember().getUser().openPrivateChannel().complete();
                channel.sendMessage("N'oublie pas de participer au concours (si il y en a un en ce moment) en faisait ;gr dans #commande-bots !").queue();

            }catch(Exception e){

                event.getGuild().getTextChannelById(main.getObs().welcomeTX).sendMessage(event.getMember().getAsMention()+", impossible de vous envoyer de mp :/").queue();

            }


        },"join-"+event.getMember().getUser().getId()).start();


    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        EmbedBuilder builder = new EmbedBuilder().setColor(Color.RED).setTitle(event.getMember().getUser().getName()+" a quitté le serveur !");
        builder.setDescription("on est tous triste de ce départ \uD83D\uDE2D");

        main.getJda().getTextChannelById(main.getObs().welcomeTX).sendMessage(builder.build()).queue();
    }
}
