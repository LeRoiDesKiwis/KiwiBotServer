package fr.leroideskiwis.kiwibot.commands;

import fr.leroideskiwis.kiwibot.Main;
import fr.leroideskiwis.kiwibot.Role;
import fr.leroideskiwis.kiwibot.command.Command;
import fr.leroideskiwis.kiwibot.command.CommandCore;
import fr.leroideskiwis.kiwibot.command.SimpleCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.io.PrintStream;

public class BasicCommands {

    @Command(name="stop",type= Command.ExecutorType.ALL,role=Role.OWNER)
    public void stop(Main main, PrintStream printStream){

        printStream.println("Le bot s'est arrêté !");
        main.setRunning(false);

    }

    @Command(name="afk",description="se mettre afk")
    public void afk(Member member, JDA jda, Main main, Guild guild, TextChannel channel){

        if(member.getRoles().contains(jda.getRolesByName("afk", true).get(0))){

            guild.getController().removeSingleRoleFromMember(member, jda.getRolesByName("afk", true).get(0)).complete();

            channel.sendMessage("Vous n'êtes désormais plus afk !").queue();

        } else {

            guild.getController().addSingleRoleToMember(member, jda.getRolesByName("afk", true).get(0)).complete();
            channel.sendMessage("Vous êtes désormais afk !").queue();

        }

    }

    @Command(name="github",description = "Avoir accès au channel github")
    public void github(JDA jda, Member member, TextChannel channel, Guild guild, Main main){

        if(member.getRoles().contains(jda.getRoleById(main.getConfig("githubRole")))){

            guild.getController().removeSingleRoleFromMember(member, jda.getRoleById(main.getConfig("githubRole"))).complete();

            channel.sendMessage("Vous n'avez désormais plus accès aux channels github !").queue();

        } else {

            guild.getController().addSingleRoleToMember(member, jda.getRoleById(main.getConfig("githubRole"))).complete();
            channel.sendMessage("Vous avez désormais accès aux channels github !").queue();

        }

    }

    @Command(name="purge",role= Role.MODO)
    public void onPurge(String[] args, TextChannel channel, Member member){

        channel.getHistory().retrievePast(Integer.parseInt(args[0])).complete().forEach(m -> m.delete().complete());

        channel.sendMessage(Integer.parseInt(args[0])+" messages ont été supprimés !").queue();

    }

    //TODO ;help access pour les commandes accessibles par leurs rôles, ;command member et ;command na

    @Command(name="help")
    public void onHelp(CommandCore commandCore, Main main, Guild guild, Member member, TextChannel channel, CommandCore core){

        EmbedBuilder builder = new EmbedBuilder().setColor(Color.GREEN);
        EmbedBuilder builderA = new EmbedBuilder().setColor(Color.ORANGE);
        EmbedBuilder builderNA = new EmbedBuilder().setColor(Color.RED);

        int count = 0;
        int countO = 0;
        int countNA = 0;

        for(SimpleCommand command : core.getCommands()){

            if(command.needRole(Role.MEMBER)){

                    builder.addField(command.getName(), command.getDescription(), false);

                    count++;

                continue;
            } else if(commandCore.checkPerm(command.getNeededRole(), member, guild)){
                builderA.addField(command.getName()+" (réservé au rôle "+command.getNeededRole().toString().toLowerCase()+")", command.getDescription(), false);
                countO++;
            } else {
                builderNA.addField(command.getName()+" (réservé au rôle "+command.getNeededRole().toString().toLowerCase()+")", command.getDescription(), false);
                countNA++;
            }

        }

        builder.setTitle(count+" commandes accessibles à tous; préfixe : "+main.getPrefixe());
        builderA.setTitle(countO+" commandes accessibles à vos rôles.");
        builderNA.setTitle(countNA+" commandes non-accessibles par vous.");

        channel.sendMessage(builder.build()).queue();
        if(countO != 0) channel.sendMessage(builderA.build()).queue();
        if(countNA != 0) channel.sendMessage(builderNA.build()).queue();


    }

}
