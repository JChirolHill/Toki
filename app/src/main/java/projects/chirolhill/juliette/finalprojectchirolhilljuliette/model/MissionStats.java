package projects.chirolhill.juliette.finalprojectchirolhilljuliette.model;

public class MissionStats {
    private int timesCompleted;
    private boolean setup;
    private String kind;

    public MissionStats(String kind) {
        timesCompleted = 0;
        setup = false;
        this.kind = kind;
    }

    public int getTimesCompleted() {
        return timesCompleted;
    }

    public void incrementTimesCompleted() {
        timesCompleted++;
    }

    public void setTimesCompleted(int timesCompleted) {
        this.timesCompleted = timesCompleted;
    }

    public String getKind() {
        return kind;
    }

    public boolean isSetup() {
        return setup;
    }

    public void setSetup(boolean setup) {
        this.setup = setup;
    }
}
