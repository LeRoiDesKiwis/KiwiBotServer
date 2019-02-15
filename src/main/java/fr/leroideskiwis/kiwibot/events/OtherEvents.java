package fr.leroideskiwis.kiwibot.events;

import fr.leroideskiwis.kiwibot.Main;
import fr.leroideskiwis.kiwibot.voteban.VoteBan;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;

public class OtherEvents extends ListenerAdapter {

    private Main main;

    public OtherEvents(Main main) {

        this.main = main;

    }

    public void sendMessageToLogs(EmbedBuilder builder){

        main.getJda().getTextChannelById(main.getConfig("logTX")).sendMessage(builder.build()).queue();

    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {

        if(event.getChannel().getId().equals(main.getConfig("voteBan"))){

            VoteBan ban = new VoteBan(main);

            ban.checkAndBan(event.getGuild(), event.getChannel().getMessageById(event.getMessageId()).complete());

        }

    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        if(main.getRaidProtection().isRaidProtect()) {

            event.getMember().getUser().openPrivateChannel().complete().sendMessage("Désolé, le serveur est actuellement verrouillé. Revenez plus tard :D").queue();
            event.getGuild().getController().kick(event.getMember(), "anti-raid protect").queue();
            return;
        }

        if(main.getRaidProtection().tooFast()){

            main.getRaidProtection().setRaidProtect(true);
            return;

        }

        main.getRaidProtection().setLastJoin(System.currentTimeMillis());

        EmbedBuilder builder = new EmbedBuilder().setColor(Color.GREEN).setTitle("Bienvenue "+event.getMember().getUser().getName()+" !");
        builder.setDescription("Passe du bon temps sur notre serveur ! \uD83D\uDE01");
        main.addRoleMember(event.getGuild(), event.getMember());

        main.getJda().getTextChannelById(main.getConfig("welcomeTX")).sendMessage(builder.build()).queue();

        main.getUtils().sendPrivateMessage(main.getJda().getTextChannelById(main.getConfig("welcomeTX")), event.getMember(), "N'oublie pas de participer au concours (si il y en a un en ce moment) en faisait ;gr dans #commande-bots !", 7);


    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        EmbedBuilder builder = new EmbedBuilder().setColor(Color.RED).setTitle(event.getMember().getUser().getName()+" a quitté le serveur !");
        builder.setDescription("on est tous triste de ce départ \uD83D\uDE2D");

        main.getJda().getTextChannelById(main.getConfig("welcomeTX")).sendMessage(builder.build()).queue();
    }
}
