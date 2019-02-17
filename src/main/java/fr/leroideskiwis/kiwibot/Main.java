package fr.leroideskiwis.kiwibot;

import fr.leroideskiwis.kiwibot.command.CommandCore;
import fr.leroideskiwis.kiwibot.events.CommandEvents;
import fr.leroideskiwis.kiwibot.events.mutesEvent;
import fr.leroideskiwis.kiwibot.events.OtherEvents;
import fr.leroideskiwis.kiwibot.noraid.RaidProtection;
import fr.leroideskiwis.kiwibot.utils.Configuration;
import fr.leroideskiwis.kiwibot.utils.Utils;
import fr.leroideskiwis.kiwibot.window.LauncherWindow;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main extends ListenerAdapter implements Runnable {

    private Utils utils = new Utils();
    private String prefixe = ";";
    private JDA jda;
    private CommandCore commandCore;
    private boolean running = true;
    private Scanner scan = new Scanner(System.in);
    private boolean debug;
    private mutesEvent noraid;
    private RaidProtection raidProtection;
    public final static Configuration configuration;

    static {
        Configuration config = null;
        try {
            config = new Configuration("./config.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        configuration = config;
    }

    public mutesEvent getNoraid() {
        return noraid;
    }

    public boolean isDebug() {
        return debug;
    }

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

    public void addRoleMember(Guild g, Member m) {

        if (!m.getRoles().contains(jda.getRoleById(getConfig("membreRole")))) {

            g.getController().addSingleRoleToMember(m, jda.getRoleById(getConfig("membreRole"))).queue();

        }

    }

    @Override
    public void onReady(ReadyEvent event) {
    }

    private Main(String[] args) throws LoginException, InterruptedException, IOException {

        if (args.length != 0 && args[0].equalsIgnoreCase("debug")) debug = true;

        commandCore = new CommandCore(this);
        jda = new JDABuilder(AccountType.BOT).setToken(configuration.getString("token", "-- Insert your token here ! --")).setGame(Game.playing(configuration.getString("game", "eat cookies"))).build();
        jda.awaitReady();
        this.raidProtection = new RaidProtection();

        noraid = new mutesEvent(this);
        jda.addEventListener(new CommandEvents(this));
        if (!isDebug()) jda.addEventListener(this);
        if (!isDebug()) jda.addEventListener(new OtherEvents(this));
        if (!isDebug()) jda.addEventListener(noraid);

        for (Guild g : jda.getGuilds()) {

            for (Member m : g.getMembers()) {

                addRoleMember(g, m);

            }

        }

    }

    public static void main(String... args) {

        try {

            new Thread(new Main(args), "main-bot").start();
        } catch (Exception e) {
            e.printStackTrace();
            configuration.save();
        }
    }


    public String getPrefixe() {
        return prefixe;
    }

    public Utils getUtils() {

        return utils;

    }

    @Override
    public void run() {

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(Guild g : jda.getGuilds()){
            g.getAudioManager().closeAudioConnection();
        }

        while (running) {
            System.out.print(jda.getSelfUser().getName() + " > ");

            if (scan.hasNextLine()) commandCore.commandConsole(scan.nextLine());
            System.out.println();

        }

        configuration.save();

        jda.shutdownNow();
        System.exit(1);

    }

    public String getConfig(String path) {

        JSONObject json = configuration.getJsonObject("config", new JSONObject());

        return json.getString(path);

    }
    public RaidProtection getRaidProtection() {
        return raidProtection;
    }
}