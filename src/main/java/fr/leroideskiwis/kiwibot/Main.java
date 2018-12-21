package fr.leroideskiwis.kiwibot;

import fr.leroideskiwis.kiwibot.command.CommandCore;
import fr.leroideskiwis.kiwibot.events.CommandEvents;
import fr.leroideskiwis.kiwibot.events.OtherEvents;
import fr.leroideskiwis.kiwibot.utils.Utils;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

public class Main extends ListenerAdapter implements Runnable{

    private Utils utils = new Utils();
    private String prefixe = ";";
    private JDA jda;
    private CommandCore commandCore;
    private Objects obs;
    private boolean running = true;
    private Scanner scan = new Scanner(System.in);

    public JDA getJda() {
        return jda;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public CommandCore getCommandCore() {
        return commandCore;
    }

    private Main(String token) throws LoginException {

        jda = new JDABuilder(AccountType.BOT).setToken(token).build();
        commandCore = new CommandCore(this);
        jda.addEventListener(new CommandEvents(this));
        jda.addEventListener(this);
        jda.addEventListener(new OtherEvents(this));
        obs = new Objects();

    }

    public static void main(String... args){

        try {
            new Thread(new Main(new Privates().token), "main-bot").start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public String getPrefixe() {
        return prefixe;
    }

    public Utils getUtils(){

        return utils;

    }

    @Override
    public void run() {

        while(running){

            if(scan.hasNextLine()) commandCore.commandConsole(scan.nextLine());

        }

        jda.shutdownNow();
        System.exit(1);

    }

    public Objects getObs() {

        return obs;

    }
}
