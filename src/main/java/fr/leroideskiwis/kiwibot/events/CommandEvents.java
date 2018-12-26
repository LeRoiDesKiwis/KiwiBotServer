package fr.leroideskiwis.kiwibot.events;

import fr.leroideskiwis.kiwibot.Main;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandEvents extends ListenerAdapter {

    private final Main main;

    public CommandEvents(Main main) {

        this.main = main;

    }

    private void checkAfk(Message msg, TextChannel channel){

        for(Member m : msg.getMentionedMembers()){

            if(m.getRoles().contains(main.getJda().getRolesByName("afk", true).get(0))){

                channel.sendMessage(m.getUser().getName()+" est actuellement afk !").queue();

            }

        }

    }

    private boolean checkTextChannel(TextChannel tx){
        return tx.getId().equalsIgnoreCase(main.getConfig("botTX"));
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        String msg = event.getMessage().getContentDisplay();
        if(event.getAuthor().equals(main.getJda().getSelfUser())) return;
        if(event.getChannel() instanceof PrivateChannel) return;
        if(main.isDebug() && !event.getTextChannel().getId().equalsIgnoreCase(main.getConfig("debugChannel"))) return;

        if(checkTextChannel(event.getTextChannel()) || msg.startsWith(main.getPrefixe())) {

           main.getCommandCore().commandUser(msg.startsWith(main.getPrefixe()) ? msg.replaceFirst(main.getPrefixe(), "") : msg, event, event.getTextChannel(), event.getMember(), event.getGuild());

        }

        checkAfk(event.getMessage(), event.getTextChannel());
    }
}
