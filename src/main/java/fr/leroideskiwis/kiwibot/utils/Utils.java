package fr.leroideskiwis.kiwibot.utils;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

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

    public String format(String s, Object... objs){

        for(Object o : objs){

            s = s.replaceFirst("%s", o.toString());

        }
        return s;

    }

    public void sendPrivateMessage(Member m, String s){
        sendPrivateMessage(null, m, s);

    }

    public void sendPrivateMessage(TextChannel tx, Member member, String s){

        new Thread(() -> {

            try {

                member.getUser().openPrivateChannel().complete().sendMessage(s).complete();


            } catch (Exception e) {

                if (tx != null)
                    tx.sendMessage(member.getAsMention() + ", vous avez désactivé vos messages privés. ").queue();

            }
        }, "thread-send-mp-"+new Random().nextInt(9999)).start();

    }

    public double round(double d, int round){


        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(round, BigDecimal.ROUND_DOWN);
        d = bd.doubleValue();
        return d;

    }


}
