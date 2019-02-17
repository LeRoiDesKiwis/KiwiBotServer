package fr.leroideskiwis.kiwibot.commands;

import fr.leroideskiwis.kiwibot.Main;
import fr.leroideskiwis.kiwibot.Role;
import fr.leroideskiwis.kiwibot.command.Command;
import fr.leroideskiwis.kiwibot.voteban.VoteBan;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import org.json.JSONObject;

import java.awt.*;

public class CommandsModerator {

    @Command(name="lockdown",role=Role.ADMIN)
    public void lockDown(Main main, TextChannel channel){

         main.getRaidProtection().setRaidProtect(!main.getRaidProtection().isRaidProtect());

         channel.sendMessage("Le serveur est désormais "+(main.getRaidProtection().isRaidProtect() ? "lock" : "unlock")+" !").queue();

    }

    @Command(name="checkVoteBan")
    public void check(Guild g, Main main, String[] args, TextChannel channel){

        VoteBan v = new VoteBan(main);

        if(args.length == 0){
            channel.sendMessage(main.getUtils().getErrorEmbed("Vous devez fournir un argument !")).queue();
            return;
        }

        Message msg = g.getTextChannelById(main.getConfig("voteBan")).getMessageById(args[0]).complete();

        Member[] members = v.getVoteBan(g, msg);

        channel.sendMessage("Le voteban de "+main.getUtils().getName(members[1])+" par "+main.getUtils().getName(members[0])+" est à "+v.getPource(msg)+"%. Il doit être supérieur à 75% pour que "+main.getUtils().getName(members[1])+" sois ban !").queue();

    }

    @Command(name="voteban",role=Role.TEST_MODO)
    public void voteBan(Main main, Member m, TextChannel channel, Message msg){

        if(msg.getMentionedMembers().isEmpty()){

            channel.sendMessage("Erreur : vous devez mentionner quelqu'un !").queue();
            return;

        }

        Member target = msg.getMentionedMembers().get(0);

        if(target.equals(m)){
            channel.sendMessage("Vous ne pouvez pas vous auto-voteban !").queue();
            return;
        }

        new VoteBan(main).init(m, target);

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
