package ru.nikitasemiklit.model;

public class RisingSensor {

    private int id;
    private long timeStart;
    private long timeStop;

    public RisingSensor(int id, long timeStart, long timeStop) {
        this.id = id;
        this.timeStart = timeStart;
        this.timeStop = timeStop;
    }

    public int getId() {
        return id;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeStop() {
        return timeStop;
    }

    @Override
    public String toString (){
        return "Датчик " + (id + 1) + " переведен в режим тревоги с " + timeStart + " по " + timeStop + " в связи с повышением температуры";
    }
}
