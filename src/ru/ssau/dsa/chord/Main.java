package ru.ssau.dsa.chord;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        int m = 3;

        List<ChordNode> nodes = new ArrayList<>();

        ChordNode head = new ChordNode(3, 0);
        head.join2(null);
        nodes.add(head);

        for (int n : new int[]{1, 3}) {
            ChordNode node = new ChordNode(m, n);
            node.join2(head);
            nodes.add(node);
        }

        stabilizeAndPrint(nodes);


        ChordNode sixNode = new ChordNode(m, 6);
        sixNode.join2(head);
        nodes.add(sixNode);

        stabilizeAndPrint(nodes);

        sixNode.delete();
        nodes.remove(sixNode);

        stabilizeAndPrint(nodes);
    }

    private static void stabilizeAndPrint(List<ChordNode> nodes) {
        System.out.println("##############################");
        // 10 - магическое число, подобранное опытным путем
        // если стабилизацию выполнить меньше 10 раз, то результаты будут не такими как в статье
        for (int i = 0; i < 10; i++) {
            for (ChordNode node : nodes) {
                node.stabilize();
                node.fixFingers();
            }
        }

        for (ChordNode node : nodes) {
            System.out.println(node);
        }
        System.out.println("##############################");
    }
}
