package fr.leroideskiwis.kiwibot.events;

import fr.leroideskiwis.kiwibot.Main;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandEvents extends ListenerAdapter {

    private final Main main;

    public CommandEvents(Main main) {

        this.main = main;

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        String msg = event.getMessage().getContentDisplay();
        if(event.getChannel() instanceof PrivateChannel) return;

        if(msg.startsWith(main.getPrefixe())) {

            main.getCommandCore().commandUser(msg.replaceFirst(main.getPrefixe(), ""), event);

        }
    }
}
