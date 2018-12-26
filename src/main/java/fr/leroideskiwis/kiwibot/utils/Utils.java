package fr.leroideskiwis.kiwibot.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

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


    public double round(double d, int round){


        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(round, BigDecimal.ROUND_DOWN);
        d = bd.doubleValue();
        return d;

    }


}
