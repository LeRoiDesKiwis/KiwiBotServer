package fr.leroideskiwis.kiwibot.commands;

import fr.leroideskiwis.kiwibot.Main;
import fr.leroideskiwis.kiwibot.Role;
import fr.leroideskiwis.kiwibot.audio.AudioListener;
import fr.leroideskiwis.kiwibot.audio.AudioSender;
import fr.leroideskiwis.kiwibot.audio.SilentSender;
import fr.leroideskiwis.kiwibot.command.Command;
import fr.leroideskiwis.kiwibot.command.CommandCore;
import fr.leroideskiwis.kiwibot.command.SimpleCommand;
import fr.leroideskiwis.kiwibot.utils.Utils;
import javazoom.jl.decoder.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.AudioManager;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;
import java.awt.Color;

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

                if(ht.toString().toLowerCase().startsWith(args[0].toLowerCase())) helpType = ht;

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

    @Command(name="test")
    public void test(Guild g, Member m, TextChannel textChannel, Utils utils){

        if(!m.getVoiceState().inVoiceChannel()){

            textChannel.sendMessage(utils.getErrorEmbed("Vous devez être dans un channel vocal !")).queue();

            return;
        }

        VoiceChannel channel = m.getVoiceState().getChannel();

        g.getAudioManager().setReceivingHandler(new AudioReceiveHandler() {
            @Override
            public boolean canReceiveCombined() {
                return false;
            }

            @Override
            public boolean canReceiveUser() {
                return false;
            }

            @Override
            public void handleCombinedAudio(CombinedAudio combinedAudio) {

            }

            @Override
            public void handleUserAudio(UserAudio userAudio) {

            }
        });
        g.getAudioManager().setSendingHandler(new AudioSendHandler() {
            @Override
            public boolean canProvide() {
                return true;
            }

            @Override
            public byte[] provide20MsAudio() {
                byte[] bytes = new byte[3840];

                for(int i = 0; i < bytes.length; i++){

                    bytes[i] = (byte)new Random().nextInt(255);

                }

                return bytes;
            }
        });
        g.getAudioManager().openAudioConnection(channel);

    }

    @Command(name="record")
    public void record(Utils utils, User user, Member m, TextChannel textChannel, String[] args, Guild g, JDA jda) throws IOException, LineUnavailableException {

        if(!m.getVoiceState().inVoiceChannel()){

            textChannel.sendMessage(utils.getErrorEmbed("Vous devez être dans un channel vocal !")).queue();

            return;
        }

        VoiceChannel channel = m.getVoiceState().getChannel();

        if(!g.getAudioManager().isConnected()) {

            AudioManager manager = g.getAudioManager();

            manager.setSendingHandler(new SilentSender());
            manager.setReceivingHandler(new AudioListener(new File("test.mp3")));
            manager.openAudioConnection(channel);
            textChannel.sendMessage("Connecté au channel **"+channel.getName()+"**").queue();

        } else {
            textChannel.sendMessage("Déconnecté du channel **"+channel.getName()+"**").queue();
            AudioListener listener = (AudioListener) g.getAudioManager().getReceiveHandler();
            g.getAudioManager().closeAudioConnection();
            listener.close();
        }


    }

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
        tx.sendFile(Main.configuration.getFile(), "Voici votre config ! Ce message s'auto-supprimera dans 5 secondes.").queue(msg -> {
            try {
                Thread.sleep(5000);
                if(msg != null)
                    msg.delete().queue();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    //@Command(name="playmp3")
    public void plaympthree(Guild g, String[] args, TextChannel textChannel, Member m) throws IOException, BitstreamException, DecoderException, UnsupportedAudioFileException, LineUnavailableException {

        File file = new File(args[0]);

        for(AudioFileFormat.Type t : AudioSystem.getAudioFileTypes()){
            textChannel.sendMessage(t.toString()).queue();
        }

        AudioInputStream stream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = stream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format);

        byte[] current = new byte[1024];
        int read = 0;

        while((read = stream.read(current, 0, current.length)) != -1){

            g.getAudioManager().setReceivingHandler(new AudioReceiveHandler() {
                @Override
                public boolean canReceiveCombined() {
                    return false;
                }

                @Override
                public boolean canReceiveUser() {
                    return false;
                }

                @Override
                public void handleCombinedAudio(CombinedAudio combinedAudio) {

                }

                @Override
                public void handleUserAudio(UserAudio userAudio) {

                }
            });
            g.getAudioManager().setSendingHandler(new AudioSendHandler() {
                @Override
                public boolean canProvide() {
                    return true;
                }

                @Override
                public byte[] provide20MsAudio() {

                    return current;
                }
            });

            g.getAudioManager().openAudioConnection(m.getVoiceState().getChannel());


        }
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
