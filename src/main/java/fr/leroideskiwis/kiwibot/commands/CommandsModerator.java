package fr.leroideskiwis.kiwibot.commands;

import fr.leroideskiwis.kiwibot.Main;
import fr.leroideskiwis.kiwibot.Role;
import fr.leroideskiwis.kiwibot.command.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;

public class CommandsModerator {

    @Command(name="ban",description = "bannir quelqu'un", role= Role.MODO)
    public void ban(TextChannel channel, Message msg, String[] args, Member member, Main main, Guild guild){

        if(msg.getMentionedMembers().isEmpty()) channel.sendMessage("Vous devez mentionner quelqu'un !").queue();

        int dayDel = args.length <= 3 ? 0 : Integer.parseInt(args[2]);
        String reason = args.length <= 2 ? "Aucune raison donnée" : args[1];

        Member banned = msg.getMentionedMembers().get(0);
        EmbedBuilder builder = new EmbedBuilder().setColor(Color.RED).setAuthor("Vous avez été banni !", null, banned.getUser().getAvatarUrl()).setDescription("Raison : "+reason).setFooter("Banni par "+member.getUser().getName(), member.getUser().getAvatarUrl());


        banned.getUser().openPrivateChannel().queue(pv -> {


            pv.sendMessage(builder.build()).queue();


        });


        guild.getController().ban(banned, dayDel, reason).queue();

        channel.sendMessage(builder.setAuthor(banned.getUser().getName()+" a été banni !", null, banned.getUser().getAvatarUrl()).build()).queue();

    }

}
