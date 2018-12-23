package fr.leroideskiwis.kiwibot.utils;

import java.math.BigDecimal;

public class Utils {

    public void debug(String s){

        System.err.println("[DEBUG] "+s);

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
