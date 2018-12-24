package fr.leroideskiwis.kiwibot.command;

import fr.leroideskiwis.kiwibot.Main;
import fr.leroideskiwis.kiwibot.command.printstreams.PrintStreamChannel;
import fr.leroideskiwis.kiwibot.commands.BasicCommands;
import fr.leroideskiwis.kiwibot.commands.CommandGiveAway;
import fr.leroideskiwis.kiwibot.commands.CommandsModerator;
import fr.leroideskiwis.kiwibot.exceptions.KiwiException;
import fr.leroideskiwis.kiwibot.utils.Utils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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
    }

    private void registerCommand(Object o){

        for(Method m : o.getClass().getDeclaredMethods()){

            if(m.isAnnotationPresent(Command.class)){

                Command cmdA = m.getAnnotation(Command.class);

                commands.add(new SimpleCommand(cmdA.op(), cmdA.name(), cmdA.description(), cmdA.type(), o, m));

                main.getUtils().debug("la commande "+cmdA.name()+" a été enregistrée avec succès !");

            } else System.err.println("La méthode "+m.getName()+" n'est pas annoté de Command");

        }


    }


    public void commandConsole(String cmd){

        try{

            for(SimpleCommand simpleCommand : commands){

                if(simpleCommand.getName().equalsIgnoreCase(cmd) && (simpleCommand.getExecutorType() == Command.ExecutorType.CONSOLE || simpleCommand.getExecutorType() == Command.ExecutorType.ALL)) execute(Command.ExecutorType.CONSOLE, simpleCommand, cmd, null);

            }

        }catch(Exception ex){

            ex.printStackTrace();

        }


    }

    public String getCommand(String s){
        return s.split(" ")[0];
    }

    private String checkAliase(String cmd, String toTest){

        String finalStr = "";

        for(int i = 0; i < cmd.length(); i++){
            try {
                finalStr += toTest.toCharArray()[i];
            }catch(Exception e){
                break;
            }

        }

        return finalStr;

    }

    public void commandUser(String s, MessageReceivedEvent e){


        List<SimpleCommand> available = new ArrayList<>();

        String cmd = getCommand(s);
        try{

            for(SimpleCommand simpleCommand : commands){


                    if (!(simpleCommand.getExecutorType() == Command.ExecutorType.ALL || simpleCommand.getExecutorType() == Command.ExecutorType.USER))
                        continue;
                    if (cmd.equalsIgnoreCase(checkAliase(cmd, simpleCommand.getName()))) {

                        if(simpleCommand.needOp() && !e.getMember().equals(e.getGuild().getOwner())) continue;
                        available.add(simpleCommand);

                    }

            }

            if(available.size() == 0) return;
            else {

                if(available.size() > 1){

                    StringBuilder builder = new StringBuilder();
                    for(int i = 0; i < available.size(); i++){


                        builder.append(available.get(i).getName());
                        if(i != available.size()-1) builder.append(", ");

                    }
                    e.getTextChannel().sendMessage("Availables commands : "+builder.toString()).queue();

                } else {

                    execute(Command.ExecutorType.USER, available.get(0), e.getMessage().getContentDisplay(), e);

                }

            }


        }catch(Exception ex){

            EmbedBuilder builder = new EmbedBuilder().setColor(Color.RED).setTitle("Erreur !").setDescription(ex.getMessage());
            e.getTextChannel().sendMessage(builder.build()).queue();
            ex.printStackTrace();


        }


    }

    private String[] getArgs(String s){

        String[] commandSplit = s.split(" ");
        String[] args = new String[commandSplit.length-1];
        for(int i = 1; i < commandSplit.length; i++) args[i-1] = commandSplit[i];

        return args;

    }

    private void execute(Command.ExecutorType type, SimpleCommand simpleCommand, String cmd, MessageReceivedEvent e) throws KiwiException {

        Parameter[] parameters = simpleCommand.getMethod().getParameters();
        Object[] objects = new Object[parameters.length];
        String[] args = getArgs(cmd);

        for(int i = 0; i < parameters.length; i++){

            try {

                if (parameters[i].getType() == MessageReceivedEvent.class) objects[i] = e;
                else if (parameters[i].getType() == Channel.class) objects[i] = e.getChannel();
                else if (parameters[i].getType() == Message.class) objects[i] = e.getMessage();
                else if (parameters[i].getType() == Member.class) objects[i] = e.getMember();
                else if (parameters[i].getType() == User.class) objects[i] = e.getAuthor();
                else if (parameters[i].getType() == Guild.class) objects[i] = e.getGuild();
                else if (parameters[i].getType() == Main.class) objects[i] = main;
                else if (parameters[i].getType() == Utils.class) objects[i] = main.getUtils();
                else if (parameters[i].getType() == TextChannel.class) objects[i] = e.getTextChannel();
                else if (parameters[i].getType() == String[].class) objects[i] = args;
                else if (parameters[i].getType() == CommandCore.class) objects[i] = this;
                else if (parameters[i].getType() == JDA.class) objects[i] = main.getJda();
                else if (parameters[i].getType() == PrintStream.class) {

                    if(type == Command.ExecutorType.CONSOLE){

                        objects[i] = System.out;

                    } else if(type == Command.ExecutorType.USER){

                        objects[i] = new PrintStreamChannel(e.getTextChannel(), System.out);

                    }

                }
            }catch(NullPointerException npe){

                continue;

            }

        }

        if(type == Command.ExecutorType.CONSOLE || (!simpleCommand.needOp() || e.getAuthor().equals(e.getGuild().getOwner().getUser()))) {

            Thread thread = new Thread(() -> {



                try {
                    simpleCommand.getMethod().invoke(simpleCommand.getObject(), objects);
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }, "command-"+simpleCommand.getName()+"-"+new Random().nextInt(99999));
            thread.setDaemon(true);
            thread.start();
            main.getUtils().debug("Un nouvelle thread commande a été crée par %s : %s ", e.getMember().getUser().getName(), thread.getName());
        }
        else throw new KiwiException("Vous devez être le propriétaire du serveur pour exécuter cette commande !");
    }

}
