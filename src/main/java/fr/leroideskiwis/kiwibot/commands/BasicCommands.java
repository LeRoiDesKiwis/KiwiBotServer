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
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.activation.CommandMap;
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

    @Command(name="forcecommand",description = "forcer quelqu'un à executer une commande",role=Role.ADMIN)
    public void forceCommand(Guild guild, TextChannel channel, Main main, Message msg, CommandCore commandCore, String[] args){

        Member target = msg.getMentionedMembers().get(0);
        String str = "";

        for(int i = target.getNickname() == null ? target.getUser().getName().split(" ").length : target.getNickname().split(" ").length; i < args.length; i++){

            str+=args[i];
            if(i != args.length-1) str+=" ";

        }

        commandCore.commandUser((str.startsWith(main.getPrefixe()) ? str.replaceFirst(main.getPrefixe(), "") : str), channel, target, guild);

    }

    //TODO faire des pages pour le ;help (genre ;help MEMBER 1 etc)

    @Command(name="help")
    public void onHelp(String[] args, CommandCore commandCore, Main main, Guild guild, Member member, TextChannel channel, CommandCore core){
        HelpType helpType = null;

        try {

            for(HelpType ht : HelpType.values()){

                if(args[0].equalsIgnoreCase(commandCore.checkAliase(args[0], ht.toString().toLowerCase()))) helpType = ht;

            }

            if(helpType == null) throw new Exception();

        }catch(Exception e) {

            EmbedBuilder builder = new EmbedBuilder().setColor(Color.RED);
            builder.setTitle("Vous devez mettre un argument parmis les propositions suivantes : ");

            for(HelpType ht : HelpType.values()){

                builder.addField(ht.toString().toLowerCase(), ht.getMessage(), false);

            }

            channel.sendMessage(builder.build()).queue();

            return;

        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(helpType.getMessage());

        switch(helpType){

            case ACCESS:
                builder.setColor(Color.ORANGE);
                break;

            case NO_ACCESS:
                builder.setColor(Color.RED);
                break;

            case MEMBER:
                builder.setColor(Color.GREEN);
                break;

        }

        int count = 0;

        for(SimpleCommand command : core.getCommands()){

            switch(helpType){

                case MEMBER:

                    if(command.needRole(Role.MEMBER)) {
                        builder.addField(command.getName(), command.getDescription(), false);

                        count++;
                    }

                    break;

                case ACCESS:
                    if(!command.needRole(Role.MEMBER) && commandCore.checkPerm(command.getNeededRole(), member, guild)) {
                        builder.addField(command.getName() + " (réservé au rôle " + command.getNeededRole().toString().toLowerCase() + ")", command.getDescription(), false);
                        count++;
                    }
                    break;

                case NO_ACCESS:

                    if(!commandCore.checkPerm(command.getNeededRole(), member, guild)) {

                        builder.addField(command.getName() + " (réservé au rôle " + command.getNeededRole().toString().toLowerCase() + ")", command.getDescription(), false);
                        count++;
                    }

                    break;

            }


        }

        builder.setTitle(count+" "+helpType.getMessage());

        channel.sendMessage(builder.build()).queue();


    }

    private enum HelpType{

        MEMBER("à tout le monde"), ACCESS("aux rôles spéciaux que vous possédez"), NO_ACCESS("aux rôles que vous ne possédez pas");

        private String message;

        HelpType(String msg){
            this.message = "Commandes accessibles ";
            this.message += msg;
        }

        public String getMessage() {

            return message;
        }
    }

}
