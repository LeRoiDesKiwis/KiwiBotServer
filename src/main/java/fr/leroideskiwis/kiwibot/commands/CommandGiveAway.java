package fr.leroideskiwis.kiwibot.commands;

import fr.leroideskiwis.kiwibot.Main;
import fr.leroideskiwis.kiwibot.command.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandGiveAway {

    @Command(name="gregister", description = "s'incrire au concours")
    public void onRegister(JDA jda, Guild guild, TextChannel channel, Main main, Member member){

        if(member.getRoles().contains(jda.getRoleById(main.getObs().concoursRole))){

            channel.sendMessage("Erreur : vous êtes déjà inscrit !").queue();


        } else {
            guild.getController().addSingleRoleToMember(member, jda.getRoleById(main.getObs().concoursRole)).queue();

            channel.sendMessage("Vous êtes maintenant inscrit au concours !").queue();

        }


    }

    private List<Member> getParticipants(Main main, Guild guild, Role role){
        List<Member> members = new ArrayList<>();

        for(Member member : guild.getMembers()){

            for(Role roleM : member.getRoles()){

                if(roleM.equals(role)) {
                    members.add(member);
                    if(!main.getObs().pena.contains(member.getUser().getIdLong())) members.add(member);
                    break;
                }

            }

        }

        return members;
    }

    @Command(name="ggo", description = "Tirer au sort ou voir le nombre de participants",op=true)
    public void onGo(JDA jda, String[] args, Guild guild, TextChannel channel, Main main, Member member){

        channel.sendMessage("Veuillez patienter... Recherche des participants...").queue();
        List<Member> members = getParticipants(main, guild, jda.getRoleById(main.getObs().concoursRole));

        if(args.length != 0 && args[0].equalsIgnoreCase("confirm")){

            if(members.size() == 0) channel.sendMessage("Il n'y a aucun participant.").queue();
            else {

                Member winner = members.get(new Random().nextInt(members.size()));
                channel.sendMessage(winner.getUser().getName() + " a gagné ! Bravo à lui/elle !").queue();

                for (Member member1 : members) {

                    guild.getController().removeSingleRoleFromMember(member1, jda.getRoleById(main.getObs().concoursRole)).queue();

                }
            }

        } else {

            EmbedBuilder builder = new EmbedBuilder().setColor(Color.CYAN).setTitle("Il y a "+members.size()+" participants.");

            List<Member> memberst = new ArrayList<>();

            for(Member participant : members){

                int count = 0;

                if(memberst.contains(participant)) continue;

                for(Member member1 : members){

                    if(member1.equals(participant)) count++;

                }
                int chance = (int)((count/(double)members.size())*100.0);

                builder.addField(participant.getUser().getName(), chance+"% de chances de gagner !", true);

                memberst.add(participant);

            }

            channel.sendMessage(builder.build()).queue();
            channel.sendMessage("Faites "+main.getPrefixe()+"ggo confirm pour procéder au tirage au sort.").queue();

        }

    }

}
