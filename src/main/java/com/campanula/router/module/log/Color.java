package com.campanula.router.module.log;

public enum Color {
    BLACK(30),
    RED(31),
    GREEN(32),
    YELLOW(33),
    BLUE(34),
    MAGENTA(35),
    CYAN(36),
    WHITE(37);
    int color;

    Color(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
