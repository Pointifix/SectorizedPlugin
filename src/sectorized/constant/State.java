package sectorized.constant;

public class State {
    public static GameState gameState = GameState.INACTIVE;
    public static double time = 0;
    public static String planet;

    public enum GameState {
        ACTIVE,
        INACTIVE,
        GAMEOVER,
        LOCKED,
    }
}
