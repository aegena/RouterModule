package com.campanula.router.module.log;

public @interface Level {
    int V = java.util.logging.Level.ALL.intValue();
    int D = java.util.logging.Level.FINE.intValue();
    int I = java.util.logging.Level.INFO.intValue();
    int W = java.util.logging.Level.WARNING.intValue();
    int E = java.util.logging.Level.SEVERE.intValue();
    int A = java.util.logging.Level.OFF.intValue();
}