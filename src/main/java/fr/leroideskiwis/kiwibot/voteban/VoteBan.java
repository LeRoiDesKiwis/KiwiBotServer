package fr.leroideskiwis.kiwibot.voteban;

import fr.leroideskiwis.kiwibot.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;
import java.util.*;
import java.util.List;

public class VoteBan {
    private Main main;

    public VoteBan(Main main) {
        this.main = main;

    }

    public void checkAndBan(Guild guild, Message msg){

        if(msg.getReactions().get(0).getCount() >= 5){

            Member[] members = getVoteBan(guild, msg);

            if(!(members[0] == null || members[1] == null)) {

                main.getCommandCore().commandUser("ban " + members[1].getUser().getId() + " le peuple à voté :p", main.getJda().getTextChannelById(main.getConfig("voteBan")), members[0], guild);
            }
            msg.delete().queue();

        }

    }

    public Member[] getVoteBan(Guild g, Message msg){

        Member[] members = new Member[2];

        try {

            members[0] = main.getUtils().getMemberByName(g, msg.getEmbeds().get(0).getAuthor().getName().replaceFirst("Demande de ban par ", ""));
            members[1] = main.getUtils().getMemberByName(g, msg.getEmbeds().get(0).getTitle().replaceFirst("Demande de ban de ", ""));

        }catch(Exception e){
            return null;
        }
        return members;

    }

    public void init(Member banner, Member target){

        EmbedBuilder builder = new EmbedBuilder().setColor(Color.RED);
        builder.setAuthor("Demande de ban par "+main.getUtils().getName(banner), null, banner.getUser().getDefaultAvatarUrl());
        builder.setTitle("Demande de ban de "+main.getUtils().getName(target));

        builder.setDescription("Validez par la réaction :white_check_mark: si vous êtes pour son ban.");

        main.getJda().getTextChannelById(main.getConfig("voteBan")).sendMessage(builder.build()).queue(msg -> {

            msg.addReaction("✅").queue();

        });

    }

}
