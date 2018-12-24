package fr.leroideskiwis.kiwibot;

import fr.leroideskiwis.kiwibot.command.CommandCore;
import fr.leroideskiwis.kiwibot.events.CommandEvents;
import fr.leroideskiwis.kiwibot.events.NoRaid;
import fr.leroideskiwis.kiwibot.events.OtherEvents;
import fr.leroideskiwis.kiwibot.utils.Utils;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Main extends ListenerAdapter implements Runnable {

    private Utils utils = new Utils();
    private String prefixe = ";";
    private JDA jda;
    private CommandCore commandCore;
    private boolean running = true;
    private Scanner scan = new Scanner(System.in);
    private boolean debug;
    private NoRaid noraid;

    public NoRaid getNoraid() {
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

    private Main(String[] args) throws LoginException, InterruptedException {

        if (args.length != 0 && args[0].equalsIgnoreCase("debug")) debug = true;

        commandCore = new CommandCore(this);
        jda = new JDABuilder(AccountType.BOT).setToken(readToken()).build();
        jda.awaitReady();
        noraid = new NoRaid(this);
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

        while (running) {
            System.out.print(jda.getSelfUser().getName() + " > ");

            if (scan.hasNextLine()) commandCore.commandConsole(scan.nextLine());
            System.out.println();

        }

        jda.shutdownNow();
        System.exit(1);

    }
    public String getConfig(String path){

        try {

            File file = new File("./config");
            if (!file.exists()) file.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader(file));

            String line = br.readLine();

            while(line != null){

                if(line.startsWith(path+": ")) return line.replaceFirst(path+": ", "");

                line = br.readLine();
            }

        }catch(Throwable e){
            e.printStackTrace();

        }

        return null;

    }

    public String readToken() {

        try {

            File file = new File("./token");
            if (!file.exists()) file.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader(file));

            String token = br.readLine();
            br.close();
            return token;

        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }

    }

}
