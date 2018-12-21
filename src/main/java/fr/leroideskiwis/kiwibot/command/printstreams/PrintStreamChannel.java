package fr.leroideskiwis.kiwibot.command.printstreams;

import net.dv8tion.jda.core.entities.TextChannel;

import java.io.PrintStream;

public class PrintStreamChannel extends PrintStream{
    private final PrintStream out;
    private final TextChannel textChannel;

    public PrintStreamChannel(TextChannel textChannel, PrintStream out) {
        super(out);

        this.textChannel = textChannel;
        this.out = out;

        
    }

    @Override
    public void println(String x) {
        if(textChannel != null) textChannel.sendMessage(x).complete();
        else out.println(x);
    }
}
