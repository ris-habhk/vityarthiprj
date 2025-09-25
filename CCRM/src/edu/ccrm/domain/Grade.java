// src/edu/ccrm/domain/Grade.java
package edu.ccrm.domain;

public enum Grade {
    S("S", 10.0),
    A("A", 9.0),
    B("B", 8.0),
    C("C", 7.0),
    D("D", 6.0),
    E("E", 5.0),
    F("F", 0.0);
    
    private final String letter;
    private final double points;
    
    Grade(String l, double p) {
        this.letter = l;
        this.points = p;
    }
    
    public String getLetter() { return letter; }
    public double getPoints() { return points; }
    
    public static Grade fromPoints(double p) {
        if (p >= 9.5) return S;
        if (p >= 8.5) return A;
        if (p >= 7.5) return B;
        if (p >= 6.5) return C;
        if (p >= 5.5) return D;
        if (p >= 4.5) return E;
        return F;
    }
    
    public boolean isPassing() {
        return this != F;
    }
    
    @Override
    public String toString() {
        return letter;
    }
}