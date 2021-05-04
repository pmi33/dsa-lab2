package ru.ssau.dsa.chord;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ChordNode {

    private int id;
    private Finger[] fingerTable;
    private ChordNode predecessor;

    public ChordNode(int m, int n) {
        this.id = n;
        this.fingerTable = new Finger[m];
        for (int i = 0; i < m; i++) {
            fingerTable[i] = new Finger(m, n, i, this);
        }
        this.predecessor = this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Finger[] getFingerTable() {
        return fingerTable;
    }

    public void setFingerTable(Finger[] fingerTable) {
        this.fingerTable = fingerTable;
    }

    public ChordNode getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(ChordNode predecessor) {
        this.predecessor = predecessor;
    }

    public ChordNode getSuccessor() {
        return fingerTable[0].getNode();
    }

    public void setSuccessor(ChordNode successor) {
        fingerTable[0].setNode(successor);
    }

    public ChordNode findSuccessor(int id) {
        ChordNode node = this.findPredecessor(id);
        return node.getSuccessor();
    }

    public ChordNode findPredecessor(int id) {
        ChordNode node = this;
        while (!(idInRange(id, node.id, node.getSuccessor().id) || id == node.getSuccessor().id)) {
            node = node.closestPrecedingFinger(id);
        }
        return node;
    }

    public ChordNode closestPrecedingFinger(int j) {
        int m = fingerTable.length;
        for (int i = m - 1; i >= 0; i--) {
            ChordNode node = fingerTable[i].getNode();
            if (idInRange(node.id, id, j)) {
                return node;
            }
        }
        return this;
    }

    public void join(ChordNode node) {
        if (node != null) {
            initFingerTable(node);
            updateOthers();
        } else {
            for (Finger finger : this.fingerTable) {
                finger.setNode(this);
            }
            predecessor = this;
        }
    }

    public void initFingerTable(ChordNode node) {
        ChordNode successor = getSuccessor();
        fingerTable[0].setNode(node.findSuccessor(fingerTable[0].getStart()));
        predecessor = successor.getPredecessor();
        successor.setPredecessor(this);
        int m = fingerTable.length;
        for (int i = 0; i < m - 1; i++) {
            if (id == fingerTable[i + 1].getStart()
                    || idInRange(fingerTable[i + 1].getStart(), id, fingerTable[i].getNode().id)) {
                fingerTable[i + 1].setNode(fingerTable[i].getNode());
            } else {
                fingerTable[i + 1].setNode(node.findSuccessor(fingerTable[i + 1].getStart()));
            }
        }
    }

    public void updateOthers() {
        int m = fingerTable.length;
        for (int i = 0; i < m; i++) {
            // id - 2^i
            int j = id - (1 << i);
            if (j < 0) {
                j += 1 << m;
            }
            ChordNode p = findPredecessor(j);
            p.updateFingerTable(this, i);
        }
    }

    public void updateFingerTable(ChordNode s, int i) {
        if (id == s.id || idInRange(s.id, id, fingerTable[i].getNode().id)) {
            fingerTable[i].setNode(s);
            ChordNode p = predecessor;
            p.updateFingerTable(s, i);
        }
    }

    public void join2(ChordNode node) {
        if (node != null) {
            predecessor = null;
            setSuccessor(node.findSuccessor(this.getId()));
        } else {
            for (Finger finger : fingerTable) {
                finger.setNode(this);
            }
            this.setPredecessor(this);
        }
    }

    public void stabilize() {
        ChordNode x = getSuccessor().getPredecessor();
        if (idInRange(x.id, id, getSuccessor().id)) {
            setSuccessor(x);
        }
        getSuccessor().notify(this);
    }

    public void notify(ChordNode node) {
        if (getPredecessor() == null || idInRange(node.id, predecessor.id, id)) {
            predecessor = node;
        }
    }

    public void fixFingers() {
        Random r = new Random();
        int i = r.nextInt(fingerTable.length);
        fingerTable[i].setNode(findSuccessor(fingerTable[i].getStart()));
    }

    public ChordNode findById(int i) {
        ChordNode node = this;
        Set<ChordNode> visited = new HashSet<>();
        while (node.id != i) {
            visited.add(node);
            for (Finger f : fingerTable) {
                if (i == f.getNode().id) {
                    return f.getNode();
                }
                if (f.getIntervalStart() == i || idInRange(i, f.getStart(), f.getIntervalEnd())) {
                    node = f.getNode();
                }
            }
            // поиск зациклился
            if (visited.contains(node)) {
                return null;
            }
        }
        return node;
    }

    public void delete() {
        predecessor.setSuccessor(getSuccessor());
        getSuccessor().setPredecessor(predecessor);
        int m = fingerTable.length;
        for (int i = 0; i < m; i++) {
            // id - 2^i
            int j = id - (1 << i);
            if (j < 0) {
                j += 1 << m;
            }
            ChordNode p = findPredecessor(j);
            p.updateFingerTable(getSuccessor(), i);
        }
    }

    private boolean idInRange(int i, int a, int b) {
        int end = b, ii = i, m = fingerTable.length;
        if (a >= b) {
            // += 2^m
            end += 1 << m;
            if (a > i) {
                ii += 1 << m;
            }
        }
        return a < ii && ii < end;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Node: ");
        sb.append(id).append("\n");
        sb.append("| start | interval | node |\n").append("--------------------------\n");
        for (Finger f : fingerTable) {
            sb.append(String.format("| %5d | [%2d, %2d) | %4d |",
                    f.getStart(), f.getIntervalStart(), f.getIntervalEnd(), f.getNode().id));
            sb.append("\n");
        }
        sb.append("--------------------------");
        return sb.toString();
    }
}
