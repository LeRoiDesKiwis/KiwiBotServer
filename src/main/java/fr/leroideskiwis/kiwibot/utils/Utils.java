package fr.leroideskiwis.kiwibot.utils;

import fr.leroideskiwis.kiwibot.Role;
import net.dv8tion.jda.core.entities.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Utils {

    public void debug(String s, Object... obs){

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        System.out.println(format("[%s] [INFO] "+format(s, obs), dtf.format(now)));

    }

    public Member getMemberByName(Guild guild, String s){

        for(Member m : guild.getMembers()){

            if(m.getUser().getName().equals(s)) return m;

        }

        return null;

    }

    public String format(String s, Object... objs){

        for(Object o : objs){

            s = s.replaceFirst("%s", o.toString());

        }
        return s;

    }

    public void sendPrivateMessage(Member m, String s, int disapear){
        sendPrivateMessage(null, m, s, disapear);

    }


    public String getName(Member m){
        return m.getNickname() == null ? m.getUser().getName() : m.getNickname();
    }

    public void sendPrivateMessage(TextChannel tx, Member member, String s, double disapear){

        new Thread(() -> {

            try {

                member.getUser().openPrivateChannel().complete().sendMessage(s).complete();


            } catch (Exception e) {

                if (tx != null)
                    tx.sendMessage(member.getAsMention() + ", vous avez désactivé vos messages privés. ").queue(msg -> {

                        try {
                            if(disapear > 0) {
                                Thread.sleep((int)disapear*1000);
                                msg.delete().queue();
                            }

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                    });

            }
        }, "thread-send-mp-"+new Random().nextInt(9999)).start();

    }

    public int getEmotesSize(Message msg){

        int count = 0;

        for(MessageReaction reactionEmote : msg.getReactions()){

            count += reactionEmote.getCount();

        }

        return count;

    }

    public double round(double d, int round){


        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(round, BigDecimal.ROUND_DOWN);
        d = bd.doubleValue();
        return d;

    }


}
