package ru.ssau.dsa.chord;

public class Util {

    public static int generateStart(int m, int n, int i) {
        // (n + 2^i) % (2 % m)
        return (n + (1 << i)) % (1 << m);
    }
}
