package fr.leroideskiwis.kiwibot.commands;

import fr.leroideskiwis.kiwibot.Main;
import fr.leroideskiwis.kiwibot.Role;
import fr.leroideskiwis.kiwibot.command.Command;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.IOException;

public class CommandJSON {

    @Command(name="saveConfig", role = Role.OWNER)
    public void saveConfig(Main main, TextChannel channel){
        Main.configuration.save();
        channel.sendMessage("La configuration à bien été sauvegardée !").queue();
    }

    @Command(name="reloadConfig", role = Role.OWNER)
    public void loadConfig(TextChannel channel) throws IOException {
        Main.configuration.reload();
        channel.sendMessage("Configuration rechargée avec succès !").queue();
    }

    @Command(name="getConfig", role = Role.OWNER)
    public void getConfig(Main m, TextChannel tx){
        tx.sendFile(Main.configuration.getFile(),new MessageBuilder().setContent("Voici votre config ! Ce message s'auto-supprimera dans 10 secondes.").build()).queue(msg -> {
            try {
                Thread.sleep(10000);
                if(msg != null)
                    msg.delete().queue();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Command(name="jsonget", role= Role.OWNER)
    public void getJson(Main m, TextChannel tx, String[] args){

        tx.sendMessage(Main.configuration.getObject(args[0], null).toString()).queue();

    }

}
