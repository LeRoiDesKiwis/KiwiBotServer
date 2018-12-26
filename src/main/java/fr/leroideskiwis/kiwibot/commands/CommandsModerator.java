package fr.leroideskiwis.kiwibot.commands;

import fr.leroideskiwis.kiwibot.Main;
import fr.leroideskiwis.kiwibot.Role;
import fr.leroideskiwis.kiwibot.command.Command;
import fr.leroideskiwis.kiwibot.voteban.VoteBan;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;

public class CommandsModerator {

    @Command(name="lockdown",role=Role.ADMIN)
    public void lockDown(Main main, TextChannel channel){

         main.getRaidProtection().setRaidProtect(!main.getRaidProtection().isRaidProtect());

         channel.sendMessage("Le serveur est désormais "+(main.getRaidProtection().isRaidProtect() ? "lock" : "unlock")+" !").queue();

    }

    @Command(name="voteban",role=Role.TEST_MODO)
    public void voteBan(Main main, Member m, TextChannel channel, Message msg){

        if(msg.getMentionedMembers().isEmpty()){

            channel.sendMessage("Erreur : vous devez mentionner quelqu'un !").queue();
            return;

        }

        new VoteBan(main).init(m, msg.getMentionedMembers().get(0));

    }

    @Command(name="ban",description = "bannir quelqu'un", role= Role.MODO)
    public void ban(TextChannel channel, Message msg, String[] args, Member member, Main main, Guild guild){


        if(msg != null && msg.getMentionedMembers().isEmpty()) channel.sendMessage("Vous devez mentionner quelqu'un !").queue();

        String reason = "";

        for(int i = 1; i < args.length; i++){

            reason+= args[i]+" ";

        }

        Member banned = msg == null ? guild.getMemberById(args[0]) : msg.getMentionedMembers().get(0);
        EmbedBuilder builder = new EmbedBuilder().setColor(Color.RED).setAuthor("Vous avez été banni !", null, banned.getUser().getAvatarUrl()).setDescription("Raison : "+reason).setFooter("Banni par "+member.getUser().getName(), member.getUser().getAvatarUrl());


        banned.getUser().openPrivateChannel().queue(pv -> {


            pv.sendMessage(builder.build()).queue();


        });


        guild.getController().ban(banned, 0, reason).queue();

        channel.sendMessage(builder.setAuthor(banned.getUser().getName()+" a été banni !", null, banned.getUser().getAvatarUrl()).build()).queue();

    }

}
