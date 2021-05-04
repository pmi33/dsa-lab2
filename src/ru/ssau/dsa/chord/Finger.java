package ru.ssau.dsa.chord;

public class Finger {

    private int start;
    private int intervalStart;
    private int intervalEnd;
    private ChordNode node;

    public Finger(int m, int n, int i, ChordNode node) {
        start = Util.generateStart(m, n, i);
        intervalStart = start;
        intervalEnd = Util.generateStart(m, n, i + 1);
        this.node = node;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getIntervalStart() {
        return intervalStart;
    }

    public void setIntervalStart(int intervalStart) {
        this.intervalStart = intervalStart;
    }

    public int getIntervalEnd() {
        return intervalEnd;
    }

    public void setIntervalEnd(int intervalEnd) {
        this.intervalEnd = intervalEnd;
    }

    public ChordNode getNode() {
        return node;
    }

    public void setNode(ChordNode node) {
        this.node = node;
    }
}
