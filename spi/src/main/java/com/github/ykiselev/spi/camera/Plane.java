package com.github.ykiselev.spi.camera;

public final class Plane {

    public enum Classification {
        INSIDE, OUTSIDE, ON_PLANE
    }

    private double a, b, c, d;

    public double a() {
        return a;
    }

    public void a(double a) {
        this.a = a;
    }

    public double b() {
        return b;
    }

    public void b(double b) {
        this.b = b;
    }

    public double c() {
        return c;
    }

    public void c(double c) {
        this.c = c;
    }

    public double d() {
        return d;
    }

    public void d(double d) {
        this.d = d;
    }

    public void set(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public Classification classify(float x, float y, float z) {
        final double r = x * a + y * b + z * c + d;
        if (r < 0) {
            return Classification.OUTSIDE;
        } else if (r == 0) {
            return Classification.ON_PLANE;
        }
        return Classification.INSIDE;
    }

    @Override
    public String toString() {
        return "Plane{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                ", d=" + d +
                '}';
    }
}
