package fr.leroideskiwis.kiwibot.command;

import fr.leroideskiwis.kiwibot.Main;
import fr.leroideskiwis.kiwibot.command.printstreams.PrintStreamChannel;
import fr.leroideskiwis.kiwibot.commands.*;
import fr.leroideskiwis.kiwibot.exceptions.KiwiException;
import fr.leroideskiwis.kiwibot.utils.Utils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import fr.leroideskiwis.kiwibot.Role;

import java.awt.*;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandCore {

    private List<SimpleCommand> commands = new ArrayList<>();
    private Main main;

    public List<SimpleCommand> getCommands() {
        return commands;
    }

    public CommandCore(Main main) {
        this.main = main;
        registerCommand(new CommandGiveAway(main));
        registerCommand(new BasicCommands());
        registerCommand(new CommandsModerator());
        registerCommand(new CommandMusic());
        registerCommand(new CommandJSON());
    }

    private void registerCommand(Object o){

        for(Method m : o.getClass().getDeclaredMethods()){

            if(m.isAnnotationPresent(Command.class)){

                Command cmd = m.getAnnotation(Command.class);

                commands.add(new SimpleCommand(cmd.role(), cmd.name(), cmd.description(), cmd.type(), o, m));

                main.getUtils().debug("la commande "+cmd.name()+" a été enregistrée avec succès !");

            } else System.err.println("La méthode "+m.getName()+" n'est pas annoté de Command");

        }


    }


    public void commandConsole(String cmd){

        try{

            for(SimpleCommand simpleCommand : commands){

                if(simpleCommand.getName().equalsIgnoreCase(cmd) && (simpleCommand.getExecutorType() == Command.ExecutorType.CONSOLE || simpleCommand.getExecutorType() == Command.ExecutorType.ALL)) execute(null, Command.ExecutorType.CONSOLE, simpleCommand, cmd, null);

            }

        }catch(Exception ex){

            ex.printStackTrace();

        }


    }

    public String getCommand(String s){
        return s.split(" ")[0];
    }

    /*public String checkAliase(String cmd, String toTest){

        String finalStr = "";

        for(int i = 0; i < cmd.length(); i++){
            try {
                finalStr += toTest.toCharArray()[i];
            }catch(Exception e){
                break;
            }

        }

        return finalStr;

    }*/


    public boolean checkPerm(Role role, Member member, Guild g){

        int perm = 0;

        if(role == Role.MEMBER) return true;

        if(member.getRoles().contains(g.getRoleById(role.getId())))
            return true;

        for(Role r : Role.values()){

            if(getPlace(r) > perm && member.getRoles().contains(main.getJda().getRoleById(r.getId()))) perm = getPlace(r);

        }

        return member.getUser().equals(main.getJda().getSelfUser()) || member.equals(g.getOwner()) || role == Role.MEMBER || getPlace(role) <= perm;

    }

    public void commandUser(String s, TextChannel channel, Member m, Guild guild){

        commandUser(s, null, channel, m, guild);

    }


    public int getPlace(Role role){

        for(int i = 0; i < Role.values().length; i++){

            if(Role.values()[i] == role) return i;

        }

        return -1;

    }

    public void commandUser(String s, MessageReceivedEvent e, TextChannel channel, Member m, Guild guild){

        List<SimpleCommand> available = new ArrayList<>();

        String cmd = getCommand(s);
        try{

            for(SimpleCommand simpleCommand : commands){


                    if (!(simpleCommand.getExecutorType() == Command.ExecutorType.ALL || simpleCommand.getExecutorType() == Command.ExecutorType.USER))
                        continue;
                    if (simpleCommand.getName().startsWith(cmd)) available.add(simpleCommand);


            }

            if(available.size() == 0) return;
            else {

                if(available.size() > 1){

                    StringBuilder builder = new StringBuilder();
                    for(int i = 0; i < available.size(); i++){

                        builder.append(available.get(i).getName());
                        if(i != available.size()-1) builder.append(", ");

                    }
                    channel.sendMessage("Availables commands : "+builder.toString()).queue();

                } else {

                    if(checkPerm(available.get(0).getNeededRole(), m, guild)) {

                        if(e != null) execute(e, Command.ExecutorType.USER, available.get(0), s, new MessageCommandHandler((Channel)e.getChannel(), e.getMessage(), m, e.getAuthor(), guild, channel));
                        else execute(null, Command.ExecutorType.USER, available.get(0), s, new MessageCommandHandler(null, null, m, null, guild, channel));
                    }else {
                        throw new KiwiException(main.getUtils().format("Vous devez posséder le rôle %s pour exécuter cette commande !", available.get(0).getNeededRole()));

                    }

                }

            }


        }catch(Exception ex){

            if(ex.getClass() != KiwiException.class) ex.printStackTrace();

            EmbedBuilder builder = new EmbedBuilder().setColor(Color.RED).setTitle("Erreur !").setDescription(ex.getMessage());
            channel.sendMessage(builder.build()).queue();



        }


    }

    private String[] getArgs(String s){

        String[] commandSplit = s.split(" ");
        String[] args = new String[commandSplit.length-1];
        for(int i = 1; i < commandSplit.length; i++) args[i-1] = commandSplit[i];

        return args;

    }

    private void execute(MessageReceivedEvent ev, Command.ExecutorType type, SimpleCommand simpleCommand, String cmd, MessageCommandHandler e) throws KiwiException, InvocationTargetException, IllegalAccessException {

        Parameter[] parameters = simpleCommand.getMethod().getParameters();
        Object[] objects = new Object[parameters.length];
        String[] args = getArgs(cmd);


        for (int i = 0; i < parameters.length; i++) {

            try {

                if (parameters[i].getType() == MessageReceivedEvent.class) objects[i] = ev;
                else if (parameters[i].getType() == String.class) objects[i] = cmd;
                else if (parameters[i].getType() == Channel.class) objects[i] = e.getChannel();
                else if (parameters[i].getType() == Message.class) objects[i] = e.getMessage();
                else if (parameters[i].getType() == Member.class) objects[i] = e.getMember();
                else if (parameters[i].getType() == User.class) objects[i] = e.getUser();
                else if (parameters[i].getType() == Guild.class) objects[i] = e.getGuild();
                else if (parameters[i].getType() == Main.class) objects[i] = main;
                else if (parameters[i].getType() == Utils.class) objects[i] = main.getUtils();
                else if (parameters[i].getType() == TextChannel.class) objects[i] = e.getTextChannel();
                else if (parameters[i].getType() == String[].class) objects[i] = args;
                else if (parameters[i].getType() == CommandCore.class) objects[i] = this;
                else if (parameters[i].getType() == JDA.class) objects[i] = main.getJda();
                else if (parameters[i].getType() == CommandCore.class) objects[i] = this;
                else if (parameters[i].getType() == PrintStream.class) {

                    if (type == Command.ExecutorType.CONSOLE) {

                        objects[i] = System.out;

                    } else if (type == Command.ExecutorType.USER) {

                        objects[i] = new PrintStreamChannel(e.getTextChannel() == null ? null : e.getTextChannel(), System.out);

                    }

                }
            } catch (NullPointerException npe) {

                continue;

            }

        }


        if (e != null) {
            Thread thread = new Thread(() -> {


                try {
                    simpleCommand.getMethod().invoke(simpleCommand.getObject(), objects);
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }, "command-" + simpleCommand.getName() + "-" + new Random().nextInt(99999));
            thread.setDaemon(true);
            thread.start();
            main.getUtils().debug("Un nouvelle thread commande a été crée par %s : %s ", e.getMember().getUser().getName(), thread.getName());
        } else simpleCommand.getMethod().invoke(simpleCommand.getObject(), objects);
    }

        private class MessageCommandHandler{

            private Channel channel;
            private Message msg;
            private Member member;
            private User user;
            private Guild g;
            private TextChannel tx;
            public MessageCommandHandler(Channel channel, Message msg, Member member, User user, Guild g, TextChannel tx) {
                this.channel = channel;
                this.msg = msg;
                this.member = member;
                this.user = user;
                this.g = g;
                this.tx = tx;
            }

            public Channel getChannel() {
                return channel;
            }

            public Message getMessage() {
                return msg;
            }

            public Member getMember() {
                return member;
            }

            public User getUser() {
                return user;
            }

            public Guild getGuild() {
                return g;
            }

            public TextChannel getTextChannel() {
                return tx;
            }

        }

    }



