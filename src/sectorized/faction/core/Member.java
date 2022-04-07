package sectorized.faction.core;

import mindustry.gen.Player;

public class Member {
    public Player player;
    public Faction faction;
    public boolean online = false;
    public MemberState state = MemberState.WAITING;

    public int wins = 0, score = 100, rank = -1;

    public Member(Player player) {
        this.player = player;
    }

    public enum MemberState {
        ALIVE,
        ELIMINATED,
        WAITING
    }
}
