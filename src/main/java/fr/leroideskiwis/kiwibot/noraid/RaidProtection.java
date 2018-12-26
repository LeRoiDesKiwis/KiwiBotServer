package fr.leroideskiwis.kiwibot.noraid;

public class RaidProtection {

    private boolean raidProtect;
    private double lastJoin;

    public boolean isRaidProtect() {

        return raidProtect;
    }

    public double getLastJoin() {
        return lastJoin;
    }

    public void setLastJoin(Long lastJoin) {
        this.lastJoin = lastJoin;
    }

    public boolean tooFast(){

        return lastJoin != 0.0 && (double)System.currentTimeMillis()/1000.0-getLastJoin()/1000.0 < 1.5;

    }

    public void setRaidProtect(boolean raidProtect) {
        this.raidProtect = raidProtect;
    }

}
