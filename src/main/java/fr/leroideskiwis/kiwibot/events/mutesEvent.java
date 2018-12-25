package fr.leroideskiwis.kiwibot.events;

import fr.leroideskiwis.kiwibot.CustomMember;
import fr.leroideskiwis.kiwibot.Main;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class mutesEvent extends ListenerAdapter {

    private final Main main;
    private Map<CustomMember, Long> howLong = new HashMap<>();

    public mutesEvent(Main main) {

        this.main = main;

    }

    public CustomMember getCustomMember(Member m){

        for(CustomMember key : howLong.keySet()){

            try {

                if (key.getMember().equals(m)) return key;

            }catch(Exception e){continue;}

        }

        return null;
    }

    public Map<CustomMember, Long> getHowLong() {
        return howLong;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if(event.getAuthor().equals(main.getJda().getSelfUser())) return;

        Member m = event.getMember();

        if(event.getMessage().getContentDisplay().contains("discord") && event.getMessage().getContentDisplay().contains("http")){
            event.getMessage().delete().queue();
            EmbedBuilder builder = new EmbedBuilder().setColor(Color.RED);
            builder.setTitle("Pub détecté !");
            builder.setDescription("Nous suspectons le message que vous venez d'envoyer de contenir une pub Discord, nous l'avons donc supprimé. Merci de contacter un administrateur si vous pensez qu'il y a une erreur.");

            event.getTextChannel().sendMessage(builder.build()).queue();
        }

        if(getCustomMember(m) == null) howLong.put(new CustomMember(m), System.currentTimeMillis());
        else {

            CustomMember cm = getCustomMember(m);

            int MAX = 4;

            if(cm.tooFast()){

                event.getTextChannel().sendMessage(main.getUtils().format("%s, Parle pas si vite mec ! Avec moi : on expire, on inspire, on expire, on inspire... (%s/%s avant le mute !)", m.getAsMention(), cm.getTime(), MAX)).queue();
                cm.setTime(cm.getTime()+1);
            }

            if(cm.getTime() >= MAX) {

                cm.setTime(1);
                event.getTextChannel().sendMessage(m.getAsMention() + ", Tu l'as cherché hein :/ ! Tu es maintenant mute :grin:").queue();
                event.getGuild().getController().addSingleRoleToMember(event.getMember(), main.getJda().getRoleById(main.getConfig("muteRole"))).queue();

            }

            cm.reload();

        }

    }
}
