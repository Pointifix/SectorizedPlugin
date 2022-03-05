package main.generation;

import arc.math.Mathf;

public class Utils {
    public static double normalizeSimplex(double value) {
        return Mathf.clamp((value - 0.2) * (1.0 / 0.6), 0, 1);
    }
}
