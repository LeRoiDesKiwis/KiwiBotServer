package fr.leroideskiwis.kiwibot;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

public class CustomMember {

    private final Member member;
    private int time;
    private double[] twoLastTimes = new double[]{0D, 0D, 0D};

    public CustomMember(Member m){

        this.member = m;
        this.twoLastTimes[0] = System.currentTimeMillis()/1000;

    }

    public void setTime(int time){
        this.time = time;

    }

    public int getTime(){
        return time;
    }

    public void reload(){
        if(twoLastTimes[0] == 0) twoLastTimes[0] = System.currentTimeMillis()/1000;


            double tmp = twoLastTimes[1];

            twoLastTimes[1] = twoLastTimes[0];
            twoLastTimes[2] = tmp;
            twoLastTimes[0] = System.currentTimeMillis()/1000;


    }

    public boolean tooFast(){

        if(twoLastTimes[0] == 0 && twoLastTimes[1] == 0 && twoLastTimes[2] == 0) return false;



        double total = 0;

        for(double d : twoLastTimes){

            total+= d;

        }

        double moyenne = total/3.0;

        boolean bool = System.currentTimeMillis()/1000 - moyenne < 2.0;

        if(bool){

            twoLastTimes[0] = 0;
            twoLastTimes[1] = 0;
            twoLastTimes[2] = 0;

        }

        return bool;

    }


    public Member getMember() {
        return member;
    }
}
