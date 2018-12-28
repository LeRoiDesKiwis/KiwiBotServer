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

        if(getPource(msg) >= 80){

            Member[] members = getVoteBan(guild, msg);

            if(!(members[0] == null || members[1] == null)) {

                main.getCommandCore().commandUser("ban " + members[1].getUser().getId() + " le peuple à voté :p", main.getJda().getTextChannelById(main.getConfig("voteBan")), members[0], guild);
            }
            msg.delete().queue();

        }

    }

    public double getPource(Message msg){
        return main.getUtils().getEmotesSize(msg) >= 5 ? main.getUtils().round(((double)msg.getReactions().get(0).getCount()/(double)main.getUtils().getEmotesSize(msg))*100.0, 2) : 0;
    }

    /**
     *
     * @param g
     * @param msg
     * @return members[0] = banner & members[1] = votebanned
     */

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

        builder.setDescription("Validez par la réaction :white_check_mark: si vous êtes pour son ban et validez la réaction ❌ si vous êtes contre. Il faut au moins 75% de pour pour être ban (tant qu'il n'y a pas au moins 5 réactions, le pourcentage sera 0%");

        main.getJda().getTextChannelById(main.getConfig("voteBan")).sendMessage(builder.build()).queue(msg -> {

            msg.addReaction("✅").queue();
            msg.addReaction("❌").queue();

        });

    }

}
