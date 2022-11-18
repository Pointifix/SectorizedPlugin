package sectorized.constant;

import sectorized.faction.core.Member;

public class State {
    public static GameState gameState = GameState.INACTIVE;
    public static double time = 0;
    public static String planet;
    public static Member winner = null;

    public enum GameState {
        ACTIVE,
        INACTIVE,
        GAMEOVER,
        LOCKED,
    }
}
